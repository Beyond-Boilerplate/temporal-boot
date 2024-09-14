import requests
import random
import time
from datetime import datetime, timedelta
from concurrent.futures import ThreadPoolExecutor

# API endpoint
url = 'http://localhost:8080/api/transactions/schedule'

# Headers
headers = {
    'Content-Type': 'application/json',
    'Accept-Language': 'fr'
}

# Initial time for 'when'
base_time = datetime.strptime("2024-09-14 02:30", "%Y-%m-%d %H:%M")

# Function to add random minutes (2 to 5) to the base time
def get_new_time():
    minutes_to_add = random.randint(2, 5)
    new_time = base_time + timedelta(minutes=minutes_to_add)
    return new_time.strftime("%Y-%m-%d %H:%M")

# Function to send a single request
def send_request(i):
    new_time = get_new_time()
    data = {
        "from": "UserA",
        "to": "UserB",
        "amount": 10,
        "when": new_time
    }
    response = requests.post(url, headers=headers, json=data)
    print(f"Request {i} sent with time: {new_time}, Status Code: {response.status_code}")

# Main function to send 500 requests at 30 requests per second
def send_requests():
    total_requests = 500000000
    requests_per_second = 500

    with ThreadPoolExecutor(max_workers=requests_per_second) as executor:
        for i in range(0, total_requests, requests_per_second):
            # Send 30 requests in parallel
            executor.map(send_request, range(i, i + requests_per_second))
            # Sleep for 1 second to maintain 30 requests per second
            time.sleep(1)

if __name__ == "__main__":
    send_requests()
