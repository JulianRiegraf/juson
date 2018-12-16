# postjson
A simple JSON to PostgreSQL mapper.

[![Build Status](https://travis-ci.com/JulianRiegraf/postjson.svg?token=ziwkzZeesRqGqDpdiqQf&branch=master)](https://travis-ci.com/JulianRiegraf/postjson)

## Array Mapping

juson mapps a JOSN like this:

*numbers.json*
```JSON
{
   "doubles":[
      1.1,
      1.2,
      1.3
   ],
   "ints":[
      1,
      2,
      3
   ]
}
```

into the following database tables:

**numbers**

| numbers_id |
| ---------- |
| 1001       |

**numbers_ints**

| numbers_id | ints_id |
| ---------- | ------- |
| 1001       | 2001    |
| 1001       | 2002    |
| 1001       | 2003    |

**numbers_doubles**

| numbers_id | doubles_id |
| ---------- | ---------- |
| 1001       | 3001       |
| 1001       | 3002       |
| 1001       | 3003       |

**ints**

| ints_id | ints |
| ------- | ---- |
| 2001    | 1    |
| 2002    | 2    |
| 2003    | 3    |
  

**doubles**

| doubles_id | doubles |
| ---------- | ------- |
| 3001       | 1.1     |
| 3002       | 1.2     |
| 3003       | 1.3     |