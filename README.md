新闻爬虫，将http://xw.qq.com/simple/s/index/index.htm下的几种栏目的新闻内容爬下来
栏目: 新闻、体育、财经、娱乐、房产

1. create project
--------
```bash
mvn archetype:create -DgroupId=com.app.lgr -DartifactId=spider
```

2. create tables in mysql
---------
>
```sql
SET SQL_SAFE_UPDATES = 0;
CREATE TABLE `news_category` (
	`id` bigint NOT NULL AUTO_INCREMENT,
	`name` varchar(20) NOT NULL,
	`url` varchar(255) NOT NULL,
	`desc` varchar(255),
	PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
INSERT INTO `news_category` (`name`,`url`, `desc`)
VALUES
('新闻','http://xw.qq.com/simple/s/news/index.htm','新闻栏目')
,('财经','http://xw.qq.com/simple/s/finance/index.htm','财经栏目')
,('娱乐','http://xw.qq.com/simple/s/ent/index.htm','娱乐栏目')
,('体育','http://xw.qq.com/simple/s/sports/index.htm','体育栏目');
CREATE TABLE `news_item` (
     `id`	bigint NOT NULL AUTO_INCREMENT,
     `title` varchar(100) NOT NULL,
     `content` text,
     `source` varchar(100),
     `create_time` datetime,
     `hits` bigint,
     `category_id` bigint NOT NULL,
	 PRIMARY KEY (`id`),
     KEY `FK3728B9281A2` (`category_id`),
     CONSTRAINT `FK3728B9281A2` FOREIGN KEY (`category_id`) REFERENCES `news_category` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8;
```
>
3. build and run
-------------
```bash
git clone  https://github.com/whxiyi100829/spider.git
cd spider
mvn assembly:assembly -DskipTests
cd target/spider-1.0-SNAPSHOT
# vim conf/config.properties
vim conf/config.properties
# modify bin/startSpider.sh
vim bin/startSpider.sh
# run
sh bin/startSpider.sh
```

