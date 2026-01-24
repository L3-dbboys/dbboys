### pdo_gbasedbt中text和clob类型处理  
默认的连接参数中，处理text或者clob之类的大对象或者智能大对象是以stream来处理的，将返回resource(4) of type (stream)。    
```php
<?php
    header('Content-type:text/html;charset=utf-8');
    $dbh = new PDO("gbasedbt:HOST=192.168.0.57;SERV=9088;PROT=onsoctcp;SRVR=gbase01;DB=testdb;ODTYP=1;DLOC=zh_CN.utf8;CLOC=zh_CN.utf8","gbasedbt","GBase
123$%");
    # 这里临时注释掉
    # $dbh->setAttribute(PDO::ATTR_STRINGIFY_FETCHES, true);
    $drop = 'drop table if exists animals';
    $res = $dbh->exec( $drop );

    $create = 'CREATE TABLE animals (id INTEGER, my_clob text, my_blob byte)';
    $res = $dbh->exec( $create );

    $stmt = $dbh->prepare('insert into animals (id,my_clob,my_blob) values (:id,:my_clob,:my_blob)');
    $clob = "test clob data";
    $blob = "test blob data";
    print "inserting from php variable\n";
    $stmt->bindValue( ':id' , 0 );
    $stmt->bindParam( ':my_clob' , $clob , PDO::PARAM_LOB , strlen($clob) );
    $stmt->bindParam( ':my_blob' , $blob , PDO::PARAM_LOB , strlen($blob) );
    $stmt->execute();

    $stmt = $dbh->prepare( 'select id,my_clob,my_blob from animals' );
    $res = $stmt->execute();
    $res = $stmt->fetchAll();
    var_dump( $res );
```
执行结果：  
```text
inserting from php variable
array(1) {
  [0]=>
  array(6) {
    ["ID"]=>
    string(1) "0"
    [0]=>
    string(1) "0"
    ["MY_CLOB"]=>
    resource(4) of type (stream)
    [1]=>
    resource(4) of type (stream)
    ["MY_BLOB"]=>
    resource(5) of type (stream)
    [2]=>
    resource(5) of type (stream)
  }
}
```
如果需要正常处理，需要在dbh的连接属性中加上PDO::ATTR_STRINGIFY_FETCHES，值为true  
```php
$dbh->setAttribute(PDO::ATTR_STRINGIFY_FETCHES, true);
```
再次执行，则正常   
```text
inserting from php variable
array(1) {
  [0]=>
  array(6) {
    ["ID"]=>
    string(1) "0"
    [0]=>
    string(1) "0"
    ["MY_CLOB"]=>
    string(14) "test clob data"
    [1]=>
    string(14) "test clob data"
    ["MY_BLOB"]=>
    string(14) "test blob data"
    [2]=>
    string(14) "test blob data"
  }
}
```