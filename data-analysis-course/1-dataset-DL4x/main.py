import csv
import glob

import scripts.config as config
from scripts.parser import Parser

from pandas import concat, DataFrame
from sklearn.preprocessing import OneHotEncoder, MinMaxScaler

file_prefix = 'superstep_sneakers'

def write_tsv(parser):
    tsv_header = [
        'Название',
        'Цена, руб.',
        'Бренд',
        'Цвет',
        'Страна производитель',
        'Добавили в корзину, чел.',
        'Ссылка на изображение',
    ]

    tsv_file = f'output/{file_prefix}.tsv'

    with open(tsv_file, 'wt') as out:
        tsv_writer = csv.writer(out, delimiter='\t')
        tsv_writer.writerow(tsv_header)
        for file in glob.glob('data/page*/item_page_*.html'):
            with open(file, 'r') as f:
                tsv_writer.writerow(parser.parse_item(f.read()))


def write_arff(_):
    raw_data = []
    brand_nominal = set()
    color_nominal = set()
    country_nominal = set()

    def write_set(s):
        return '{' + ','.join(s) + '}'

    def predicate(i, attr):
        return attr != '?' and i in (0, 6)

    def format_spaces(attr):
        return f'\'{attr}\'' if ' ' in attr else attr

    def add_nominal(i, r, s):
        r[i] = format_spaces(r[i])
        if r[i] != '?': s.add(r[i])

    def arff_format(parsed):
        return [f"'{attr}'" if predicate(i, attr)
                else attr for (i, attr) in enumerate(parsed)]

    with open(f'output/{file_prefix}.tsv', 'r') as tsv:
        next(tsv)
        while True:
            line = tsv.readline()
            if not line:
                break
            row = line.rstrip().split('\t')
            add_nominal(2, row, brand_nominal)
            add_nominal(3, row, color_nominal)
            add_nominal(4, row, country_nominal)
            raw_data.append(row)

    arff_file = f'output/{file_prefix}.arff'

    arff_header = \
        '% 1. Title: SuperStep Sneakers Database\n' \
        '%\n' \
        '% 2. Sources:\n' \
        '%      (a) Creator: Egor Shulpin\n' \
        '%      (b) Date: October 2024\n' \
        '@RELATION superstep\n' \
        '\n' \
        '% Название модели\n' \
        '@ATTRIBUTE title STRING\n' \
        '% Цена в рублях\n' \
        '@ATTRIBUTE price NUMERIC\n' \
        '% Бренд модели\n' \
        f'@ATTRIBUTE brand {write_set(brand_nominal)}\n' \
        '% Цвет модели\n' \
        f'@ATTRIBUTE color {write_set(color_nominal)}\n' \
        '% Страна производитель\n' \
        f'@ATTRIBUTE country_of_origin {write_set(country_nominal)}\n' \
        '% Количество людей, добавивших в корзину\n' \
        '@ATTRIBUTE added_to_cart NUMERIC\n' \
        '% Путь к изображению модели\n' \
        '@ATTRIBUTE image_path STRING\n' \
        '\n' \
        '@DATA\n'

    with open(arff_file, 'wt') as out:
        out.write(arff_header)
        arff_writer = csv.writer(out, delimiter=',')
        for row in raw_data:
            arff_writer.writerow(arff_format(row))


def write_csv(_):
    csv_header = [
        'Бренд',
        'Цена, руб.',
        'Цвет',
        'Страна производитель',
        'Добавили в корзину, чел.',
        'Ссылка на изображение',
    ]

    numeric = [
        'Цена, руб.',
        'Добавили в корзину, чел.',
    ]

    categories = [
        'Цвет',
        'Страна производитель',
    ]

    def format_line(l: str):
        row = l.rstrip().split(',')[1:]
        row[0], row[1] = row[1], row[0]
        return [s.strip('\'') for s in row]

    def fill_in_blanks(dt: DataFrame):
        for col in dt.columns:
            mode = dt[col].mode()[0]
            dt[col] = dt[col].replace('?', mode)

    def one_hot_encode(dt: DataFrame):
        encoder = OneHotEncoder(
            dtype=int,
            drop='first',
            sparse_output=False,
        )
        encoded_cat = encoder.fit_transform(dt[categories])
        encoded_data = DataFrame(
            encoded_cat,
            columns=encoder.get_feature_names_out(categories),
        )

        return concat([dt.drop(columns=categories), encoded_data], axis=1)

    def normalize_numeric(dt: DataFrame):
        normalizer = MinMaxScaler()
        dt[numeric] = normalizer.fit_transform(dt[numeric]).round(8)


    line = ''
    with open(f'output/{file_prefix}.arff') as arff:
        while line != '@DATA\n':
            line = arff.readline()

        lines = [format_line(l) for l in arff.readlines()]

        data = DataFrame(lines, columns=csv_header)

        fill_in_blanks(data)
        data = one_hot_encode(data)
        normalize_numeric(data)

        data.to_csv(f'output/{file_prefix}.csv', index=False)


def main():
    parser = Parser(config)

    parser.download_page(
        num_start_page=1,
        num_pages_to_save=40,
    )

    for file in glob.glob('data/result_page*.html'):
        with open(file, 'r') as f:
            parser.download_items(f.read())

    write_tsv(parser)

    write_arff(parser)

    write_csv(parser)


if __name__ == '__main__':
    main()
