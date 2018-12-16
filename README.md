# JUSON
A simple JSON to PostgreSQL mapper.

Status: **`DEVELOPMENT`**

[![Build Status](https://travis-ci.com/JulianRiegraf/postjson.svg?token=ziwkzZeesRqGqDpdiqQf&branch=master)](https://travis-ci.com/JulianRiegraf/postjson)

## Simple JSON object mapping

*number.json*
```JSON
{
   "type":"double",
   "positiv":true,
   "value":1.2
}
```

**number**

| type   | positiv | value |
| ------ | ------- | ----- |
| double | true    | 1.2   |

## Simple JSON array mapping

*numbers.json*
```JSON
[
   {
      "type":"double",
      "positiv":true,
      "value":12
   },
   {
      "type":"int",
      "positiv":true,
      "value":2
   },
   {
      "type":"double",
      "positiv":false,
      "value":-1.2
   }
]
```

**numbers**

| type   | positiv | value |
| ------ | ------- | ----- |
| double | true    | 1.2   |
| int    | true    | 2     |
| double | false   | -1.2  |

## Nested object mapping

*api_call.json*
```JSON
{
   "status":"OK",
   "result":{
      "type":"double",
      "positiv":true,
      "value":1.2
   }
}
```

**api_call**

| api_call_id | status |
| ----------- | ------ |
| 1001        | OK     |

**api_call_result**

| api_call_id | result_id |
| ----------- | --------- |
| 1001        | 2001      |


**result**

| result_id | type   | positiv | value |
| --------- | ------ | ------- | ----- |
| 2001      | double | true    | 1.2   |

## Nested objects and arrays

*articles_call.json*
```JSON
{
  "status": "OK",
  "last_updated": "2018-12-15T17:27:12-05:00",
  "num_results": 2,
  "results": [
    {
      "section": "World",
      "title": "Fancy title here",
      "item_type": "Article",
      "published_date": "2018-12-15T12:05:59-05:00",
      "multimedia": [
        {
          "format": "Standard Thumbnail",
          "height": 75,
          "width": 75,
          "type": "image"
        },
        {
          "format": "thumbLarge",
          "height": 150,
          "width": 150,
          "type": "image"
        }
      ]
    },
    {
      "section": "Europe",
      "item_type": "Article",
      "title": "Another fancy title",
      "published_date": "2018-12-15T16:41:18-05:00",
      "multimedia": [
        {
          "format": "Normal",
          "height": 132,
          "width": 190,
          "type": "image",
          "file": "png"
        },
        {
          "format": "mediumThreeByTwo210",
          "height": 140,
          "width": 210,
          "type": "photo"
        }
      ]
    }
  ]
}
```

**articles_call**

| articles_call_id | status | last_updated              | num_results |
| ---------------- | ------ | ------------------------- | ----------- |
| 1001             | OK     | 2018-12-15T17:27:12-05:00 | 2           |

**results**

| results_id | section | title               | item_type | published_date            |
| ---------- | ------- | ------------------- | ----------| ------------------------- |
| 2001       | World   | Fancy title here    | Article   | 2018-12-15T12:05:59-05:00 |
| 2002       | Europe  | Another fancy title | Article   | 2018-12-15T16:41:18-05:00 |


**multimedia**

| multimedia_id | format              | height | width | type  | file |
| ------------- | ------------------- | ------ | ----- | ----- | ---- |
| 3001          | Standard Thumbnail  | 75     | 75    | image | null |
| 3002          | thumbLarge          | 150    | 150   | image | null |
| 3003          | Normal              | 132    | 190   | image | png  |
| 3004          | mediumThreeByTwo210 | 140    | 210   | photo | null |

**articles_call_results**

| articles_call_id | results_id |
| ---------------- | ---------- |
| 1001             | 2001       |
| 1001             | 2002       |

  
**results_multimedia**

| results_id | multimedia_id |
| ---------- | ------------- |
| 2001       | 3001          |
| 2001       | 3002          |
| 2002       | 3003          |
| 2002       | 3004          |