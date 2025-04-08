import pandas as pd
import json
import time
import os
import requests
from concurrent.futures import ThreadPoolExecutor, as_completed
from tqdm import tqdm

# 엑셀 파일 경로
FILE_PATH = "/Users/seungwan/Downloads/유치원데이터250310.xlsx"  # 파일명 수정 가능
CACHE_FILE = "/Users/seungwan/Downloads/address_cache.json"
BATCH_SIZE = 100  # 카카오 API 제한을 고려하여 작게 설정
MAX_WORKERS = 3  # 동시 요청 수를 제한

def load_cache():
    if os.path.exists(CACHE_FILE):
        try:
            with open(CACHE_FILE, 'r', encoding='utf-8') as f:
                return json.load(f)
        except:
            print("❌ 캐시 파일 로드 실패, 새 캐시를 생성합니다")
    return {}

def save_cache():
    with open(CACHE_FILE, 'w', encoding='utf-8') as f:
        json.dump(address_cache, f, ensure_ascii=False, indent=2)
    print(f"💾 주소 캐시 저장 완료: {len(address_cache)}개 주소")

def preprocess_row(row):
    return {
        "name": str(row.get('유치원명', '')).strip(),
        "address": str(row.get('주소', '')).strip(),
        "latitude": None,
        "longitude": None
    }

def geocode_address_kakao(item):
    address = item["address"]
    name = item["name"]

    if not address:
        return item

    # 캐시 확인
    if address in address_cache:
        item["latitude"], item["longitude"] = address_cache[address]
        return item

    # 카카오 API URL과 헤더 설정
    url = "https://dapi.kakao.com/v2/local/search/address.json"
    headers = {"Authorization": "KakaoAK 카카오 API 키"}

    # 주소로 먼저 검색
    for attempt in range(3):  # 최대 3번 재시도
        try:
            params = {"query": address}
            response = requests.get(url, headers=headers, params=params)

            if response.status_code == 200:
                result = response.json()
                if result["documents"]:
                    # 성공적으로 좌표를 찾음
                    x = float(result["documents"][0]["x"])  # 경도
                    y = float(result["documents"][0]["y"])  # 위도
                    item["longitude"] = x
                    item["latitude"] = y
                    address_cache[address] = (y, x)
                    return item
                else:
                    # 주소로 찾지 못한 경우 유치원명과 주소 조합으로 키워드 검색 시도
                    keyword_url = "https://dapi.kakao.com/v2/local/search/keyword.json"
                    keyword_params = {"query": f"{name} {address.split()[0]}"}  # 유치원명과 주소 앞부분 사용

                    keyword_response = requests.get(keyword_url, headers=headers, params=keyword_params)
                    if keyword_response.status_code == 200:
                        keyword_result = keyword_response.json()
                        if keyword_result["documents"]:
                            x = float(keyword_result["documents"][0]["x"])
                            y = float(keyword_result["documents"][0]["y"])
                            item["longitude"] = x
                            item["latitude"] = y
                            address_cache[address] = (y, x)
                            return item

            # API 요청 한도를 초과했거나 다른 오류가 발생한 경우
            if response.status_code != 200:
                print(f"⚠️ API 오류 (코드 {response.status_code}): {address}")
                time.sleep(2 ** attempt)  # 지수 백오프

        except Exception as e:
            print(f"⏳ 재시도 {attempt + 1}/3: {address} ({e})")
            time.sleep(2 ** attempt)

    return item

def chunk_data(data_list, chunk_size):
    for i in range(0, len(data_list), chunk_size):
        yield data_list[i:i + chunk_size]

def process_data():
    print("🔄 데이터 전처리 중...")
    df = pd.read_excel(FILE_PATH, engine="openpyxl", header=2)
    df.rename(columns=lambda x: str(x).strip(), inplace=True)
    print("📌 컬럼명 확인:", df.columns)

    kindergartens = [preprocess_row(row) for _, row in df.iterrows()]
    print(f"총 {len(kindergartens)}개의 유치원 데이터 처리 시작")

    batches = list(chunk_data(kindergartens, BATCH_SIZE))
    all_results = []

    for batch_idx, batch in enumerate(batches, 1):
        print(f"\n⏳ 배치 {batch_idx}/{len(batches)} 처리 중... ({len(batch)}개 항목)")
        batch_results = []

        with ThreadPoolExecutor(max_workers=MAX_WORKERS) as executor:
            future_to_item = {executor.submit(geocode_address_kakao, item): item for item in batch}

            for future in tqdm(as_completed(future_to_item), total=len(batch), desc=f"배치 {batch_idx} 지오코딩"):
                try:
                    result = future.result()
                    batch_results.append(result)
                except Exception as exc:
                    print(f'처리 중 예외 발생: {exc}')

                # API 속도 제한을 피하기 위한 대기
                time.sleep(0.2)  # 카카오 API는 초당 10회 이하 요청 권장

                if len(batch_results) % 50 == 0:
                    save_cache()

        all_results.extend(batch_results)
        save_cache()

        # 각 배치 처리 후 좀 더 긴 대기 시간
        if batch_idx < len(batches):
            print(f"🕒 다음 배치 전 대기 중... (5초)")
            time.sleep(5)

    # 결과를 JSON으로 저장
    result_file = 'kindergartens_only_coords_kakao.json'
    with open(result_file, 'w', encoding='utf-8') as f:
        json.dump(all_results, f, ensure_ascii=False, indent=2)

    # 성공률 계산 및 보고
    failed_count = sum(1 for k in all_results if k['address'] and k['latitude'] is None)
    success_rate = ((len(all_results) - failed_count) / len(all_results)) * 100

    print(f"\n✅ 작업 완료!")
    print(f"📊 총 {len(all_results)}개 유치원 중 {len(all_results) - failed_count}개 주소 변환 성공 ({success_rate:.2f}%)")
    print(f"📁 결과 파일: {result_file}")

    # 실패한 항목 저장
    if failed_count > 0:
        failed_items = [k for k in all_results if k['address'] and k['latitude'] is None]
        failed_file = 'failed_geocoding_items.json'
        with open(failed_file, 'w', encoding='utf-8') as f:
            json.dump(failed_items, f, ensure_ascii=False, indent=2)
        print(f"⚠️ 변환 실패한 {failed_count}개 항목: {failed_file}")

if __name__ == "__main__":
    address_cache = load_cache()
    process_data()