# WatchService
WatchService слушает папку persons, зачитываtn эти json файлы, которые туда скидывают, конвертирует в объекты Person и вставляет в БД

# Примеры работы WatchService

Следующий файл был закинут в папку persons:

![image](https://user-images.githubusercontent.com/84938597/200936655-09ebc183-3a72-425f-a484-095611796bd2.png)

БД до того, как был добавлен файл:

![image](https://user-images.githubusercontent.com/84938597/200936946-d4847c73-3db7-47c7-8826-2a6e546195f4.png)

БД после того, как файл был добавлен:

![image](https://user-images.githubusercontent.com/84938597/200937134-6e1c5ea5-ef61-4a87-a4db-7e8bd7c2048e.png)

Если Person в файле уже существуют в БД, они игнорируются:

![image](https://user-images.githubusercontent.com/84938597/200937506-eb56b9ff-e235-4008-a7f5-8b7c41c2ba94.png)

![image](https://user-images.githubusercontent.com/84938597/200937563-895fe6fd-be3e-40d3-9ed0-fe91f14dd46c.png)
