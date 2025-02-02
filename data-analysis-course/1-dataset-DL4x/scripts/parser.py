from re import search
from bs4 import BeautifulSoup
from scripts.downloader import Downloader

class Parser:
    def __init__(self, config):
        self.config = config
        self.downloader = Downloader(
            cookies=config.cookies,
            headers=config.headers,
        )


    def download_page(
            self,
            num_start_page=1,
            num_pages_to_save=1,
    ):
        min_page_num = num_start_page
        max_page_num = num_start_page + num_pages_to_save
        for index in range(min_page_num, max_page_num):
            url = f'{self.config.page_url}{index}'
            output_file = f'data/result_page{index}.html'
            self.downloader.download(url, output_file)


    def download_items(
            self,
            html,
    ):
        soup = BeautifulSoup(
            html,
            'html.parser'
        )

        items = soup.find_all(
            'div',
            class_=self.config
            .parser_config['item_class']
        )

        for item in items:
            item_page = item.find(
                'a',
                class_=self.config
                    .parser_config['item_page_class']
            )
            item_url = item_page.get('href')
            url = self.config.root + item_url
            output_file = f'data/item_page_{url.split("/")[-2]}.html'
            self.downloader.download(url, output_file)

        self._download_images(soup)


    def _download_images(self, soup: BeautifulSoup):
        images = soup.find_all(
            'img',
            class_=self.config
                .parser_config['preview_image_class']
        )

        for image in images:
            img_url = image.get('src')
            url = self.config.root + img_url
            output_file = f'output/pics/{url.split("/")[-1]}'
            self.downloader.download(url, output_file)


    def parse_item(self, html: str) -> list:
        soup = BeautifulSoup(
            html,
            'html.parser'
        )

        self._parse_popular(soup)

        return [
            self._parse_title(soup),
            self._parse_price(soup),
            self._parse_brand(soup).upper(),
            self._parse_color(soup).upper(),
            self._parse_country(soup).upper(),
            self._parse_popular(soup),
            self._parse_image(soup),
        ]


    def _parse_title(self, soup: BeautifulSoup) -> str:
        brand = soup.find(
            'span',
            class_=self.config
            .parser_config['brand_class']
        ).findChild('font')

        return brand.get_text(strip=True)


    def _parse_brand(self, soup: BeautifulSoup) -> str:
        brand = soup.find(
            'span',
            class_=self.config
                .parser_config['brand_class']
        ).findChild('a')

        return brand.get_text(strip=True)


    def _parse_price(self, soup: BeautifulSoup) -> str:
        price = soup.find(
            'div',
            class_=self.config
                .parser_config['price_class'],
        )

        return price.get_text(strip=True).split('₽')[0].replace(' ', '')


    def _parse_color(self, soup: BeautifulSoup) -> str:
        color = soup.find(
            'span',
            class_=self.config
                .parser_config['color_class'],
        )

        if not color:
            return '?'

        match = search(
            'Цвет:\w+',
            color.get_text(strip=True),
        )

        if match:
            return match.group().split(':')[-1].strip()

        return '?'


    def _parse_country(self, soup: BeautifulSoup) -> str:
        country = soup.find(
            'div',
            class_=self.config
                .parser_config['producing_country_class']
        )

        if not country:
            return '?'

        match = search(
            'Страна производитель: \w+\n',
            country.prettify(),
        )

        if match:
            return match.group().split(': ')[-1].strip()

        return '?'


    def _parse_popular(self, soup: BeautifulSoup) -> str:
        popularity = soup.find(
            'div',
            class_=self.config
                .parser_config['popularity_class']
        )

        if not popularity:
            return '?'

        return popularity.get_text().split()[0]


    def _parse_image(self, soup: BeautifulSoup) -> str:
        article = soup.find(
            'span',
            class_=self.config
                .parser_config['article_class']
        )
        name = article.get_text().split(':  ')[-1]

        return f'pics/{name}.jpg'
