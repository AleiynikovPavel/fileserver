# API
### Отправка файла
* `POST /upload/` файл прикрепляется в `form-data` параметр `file`
#### Ответ
```
{
    "name": "Text File.txt",
    "uuid": "2c18bbc7-8776-4a16-9f67-0bdcfad2d806",
    "createAt": "2021-11-02T20:43:37.655124198",
    "newChunksPercent": 1.0
}
```
### Загрузка файла
* `POST /download/` uuid в параметре `filename`. Например `/download/?filename=2c18bbc7-8776-4a16-9f67-0bdcfad2d806`
#### Ответ
* Файл

### Список загруженных файлов
* `GET /list/`
#### Ответ
```
[
    {
        "id": 1,
        "name": "Text File.txt",
        "uuid": "41055018-e441-43db-8bd1-6d42fd79cb4a",
        "createAt": "2021-11-02T20:39:53.661558"
    },
    {
        "id": 2,
        "name": "Text File.txt",
        "uuid": "4504c261-a79b-4869-9fa0-887bd3d32a32",
        "createAt": "2021-11-02T20:40:57.204307"
    }
]
```
# Запуск
* Сервер реализован на Java 15
* Перед запуском необходимо указать акутальные параметры для подключения к БД:
  * spring.r2dbc.url
  * spring.r2dbc.username
  * spring.r2dbc.password
