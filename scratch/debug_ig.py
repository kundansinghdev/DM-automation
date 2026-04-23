import requests
import os

# From application.properties
ACCESS_TOKEN = "EAHq1IZAwSeZCsBRZAwZBDEqlicS1adY42mvxLEvQY38ceMqDNZBY3c174ZApLXaw1xEWPcYkPSaAQ8U2k41OuKd73tuPk9AmdnKPJwXXFNIF2Q070nEZBIhIWVwc3eDlPRSPYZCbeTU98wGIRynKudxaRwKVmZC6TrDMfDZC8cLaRNpamYWOZBZAC7kTFhASu5BTcQKQSKJ9hjSMW2FtWQUxc41nSvUbU7dY2IbJ2vILvp7ZAvenSuNWRgLfrQt8fcUYY7TUBUrUA57vYeksAMrsZD"
IG_ACCOUNT_ID = "17841473655798747"

def check_identity():
    url = f"https://graph.facebook.com/v19.0/me?access_token={ACCESS_TOKEN}"
    r = requests.get(url)
    print("Identity (/me):", r.json())

def find_page_id():
    # 1. Get all pages managed by the user
    url = f"https://graph.facebook.com/v19.0/me/accounts?access_token={ACCESS_TOKEN}"
    r = requests.get(url)
    data = r.json()
    if 'data' not in data:
        print("Could not fetch accounts:", data)
        return
    
    for page in data['data']:
        page_id = page['id']
        page_name = page['name']
        page_token = page['access_token']
        
        # 2. For each page, check its connected IG account
        ig_url = f"https://graph.facebook.com/v19.0/{page_id}?fields=instagram_business_account&access_token={page_token}"
        ig_r = requests.get(ig_url)
        ig_data = ig_r.json()
        
        ig_id = ig_data.get('instagram_business_account', {}).get('id')
        print(f"Page: {page_name} ({page_id}) -> IG ID: {ig_id}")
        
        if ig_id == IG_ACCOUNT_ID:
            print(f"MATCH FOUND! Page ID: {page_id}")
            print(f"Page Token: {page_token}")
            return page_id

if __name__ == "__main__":
    check_identity()
    # check_ig_account()
    find_page_id()
