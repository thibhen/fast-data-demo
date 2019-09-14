


rm -f the-proxy*.html

docker run --rm -it -v $(pwd):/root/.wapiti/generated_report --link the-proxy:the-proxy jorgeandrada/wapiti "http://the-proxy:8083/jpetstore/" --max-scan-time 5 
