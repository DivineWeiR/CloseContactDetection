import pickle

number = '66831'
flag = True
while(flag):
    url = "http://www.pythonchallenge.com/pc/def/linkedlist.php?nothing="+number
    number = requests.get(url).text.split(" ")[-1]
    print(url)
    try:
        nums = int(number)
    except:
        flag = False
url = "http://www.pythonchallenge.com/pc/def/linkedlist.php?nothing="+number
print(requests.get(url).text)