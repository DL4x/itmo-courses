import sys
import time
import requests

class Downloader:
    def __init__(
            self,
            cookies=None,
            headers=None,
    ):
        self.cookies = cookies
        self.headers = headers


    @staticmethod
    def __download_page(
            url,
            output_file,
            cookies=None,
            headers=None,
            time_to_sleep=1.5,
    ):
        try:
            response = requests.get(
                url,
                cookies=cookies,
                headers=headers,
            )
        except Exception as e:
            raise ValueError(e)

        if response.status_code == 200:
            with open(output_file, 'wb') as file:
                file.write(response.content)
            time.sleep(time_to_sleep)
        else:
            raise ValueError(f'status: <{response.status_code}>')


    def download(
            self,
            url,
            output_file,
    ):
        try:
            self.__download_page(
                url,
                output_file,
                cookies=self.cookies,
                headers=self.headers,
            )
            print(f'file by {url} saved successfully')
        except ValueError as e:
            print(f'Something went wrong: {e} while downloading {url}', file=sys.stderr)
