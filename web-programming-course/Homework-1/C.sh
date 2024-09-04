#!/bin/bash

for ((i=0; i < 101; i++))
do
	let double=i**2
	curl 'http://1d3p.wp.codeforces.com/new' \
	 -H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7' \
	 -H 'Accept-Language: ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7' \
	 -H 'Cache-Control: no-cache' \
	 -H 'Connection: keep-alive' \
	 -H 'Content-Type: application/x-www-form-urlencoded' \
	 -H 'Cookie: __utmc=71512449; __utmz=71512449.1693736224.304.26.utmcsr=yandex.ru|utmccn=(referral)|utmcmd=referral|utmcct=/; evercookie_cache=3jh164ziwm98iaq0cd; evercookie_png=3jh164ziwm98iaq0cd; evercookie_etag=3jh164ziwm98iaq0cd; 70a7c28f3de=3jh164ziwm98iaq0cd; __utma=71512449.1325461886.1667293677.1695017997.1695021289.308; JSESSIONID=1FE6399B236842CB6B2EA9D1785C8AF9' \
	 -H 'Origin: http://1d3p.wp.codeforces.com' \
	 -H 'Pragma: no-cache' \
	 -H 'Referer: http://1d3p.wp.codeforces.com/' \
	 -H 'Upgrade-Insecure-Requests: 1' \
	 -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36 OPR/100.0.0.0 (Edition Yx GX)' \
	 --data-raw '_af=34be50b38beccce4&proof='$double'&amount='$i'&submit=Submit' \
	 --compressed \
	 --insecure
done	
