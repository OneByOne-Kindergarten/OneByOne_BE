import pandas as pd
import json
import time
import os
from concurrent.futures import ThreadPoolExecutor, as_completed
from tqdm import tqdm
from geopy.geocoders import Nominatim
from geopy.exc import GeocoderTimedOut, GeocoderServiceError

# 엑셀 파일 경로
FILE_PATH = "/Users/seungwan/Downloads/유치원데이터250310.xlsx"  # 파일명 수정 가능
CACHE_FILE = "/Users/seungwan/Downloads/address_cache.json"
BATCH_SIZE = 1000
MAX_WORKERS = 5

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
        json.dump(address_cache, f, ensure_ascii=False, indent=2)  # ensure_ascii=False 추가
    print(f"💾 주소 캐시 저장 완료: {len(address_cache)}개 주소")

def preprocess_row(row):
    return {
        "name": str(row.get('유치원명', '')).strip(),
        "address": str(row.get('주소', '')).strip(),
        "latitude": None,
        "longitude": None
    }

def geocode_address(item):
    address = item["address"]
    if not address:
        return item
    
    if address in address_cache:
        item["latitude"], item["longitude"] = address_cache[address]
        return item
    
    for attempt in range(3):  # 최대 3번 재시도
        try:
            location = geolocator.geocode(address)
            if location:
                item["latitude"], item["longitude"] = location.latitude, location.longitude
                address_cache[address] = (location.latitude, location.longitude)
                save_cache()
                return item
            break
        except (GeocoderTimedOut, GeocoderServiceError) as e:
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
            future_to_item = {executor.submit(geocode_address, item): item for item in batch}
            
            for future in tqdm(as_completed(future_to_item), total=len(batch), desc=f"배치 {batch_idx} 지오코딩"):
                try:
                    result = future.result()
                    batch_results.append(result)
                except Exception as exc:
                    print(f'처리 중 예외 발생: {exc}')
                
                if len(batch_results) % 100 == 0:
                    save_cache()
        
        all_results.extend(batch_results)
        save_cache()
    
    with open('kindergartens_with_coords_all.json', 'w', encoding='utf-8') as f:
        json.dump(all_results, f, ensure_ascii=False, indent=2)
    
    print(f"✅ 총 {len(all_results)}개의 유치원 데이터를 좌표 정보와 함께 JSON으로 변환 완료!")
    
    failed_count = sum(1 for k in all_results if k['address'] and k['latitude'] is None)
    success_rate = ((len(all_results) - failed_count) / len(all_results)) * 100
    print(f"📊 주소 변환 성공률: {success_rate:.2f}% ({len(all_results) - failed_count}/{len(all_results)})")

if __name__ == "__main__":
    geolocator = Nominatim(user_agent="kindergarten_locator", timeout=10)
    address_cache = load_cache()
    process_data()
