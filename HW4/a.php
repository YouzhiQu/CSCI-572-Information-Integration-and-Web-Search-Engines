<?php
//get the q parameter from URL
$q=$_GET["q"];
$q = strtolower($q);
$q1=split (" ", $q);
$q_prefix="";
$count=count($q1);
for($y=0;$y<$count-1;$y++){
	$q_prefix=$q_prefix.$q1[$y]." ";
	//echo $q1[$y];
}
//if($count==1){
//	$url="http://localhost:8983/solr/myexample/suggest?wt=json&indent=true&q=".$q1[0];
//}
//else{
$url="http://localhost:8983/solr/myexample/suggest?wt=json&indent=true&q=".$q1[$count-1];
//}
$json=file_get_contents($url);
$sp=split (" ", $json);
#echo $sp[2];
$arrlength = count($sp);
$suggestions="<select id=\"mySelect\" onchange=\"myFunction()\">";
for($x = 0; $x < $arrlength; $x++) {
	if(substr( $sp[$x], 1, 4 ) === "term")
   { #echo $sp[$x];
   	//array_push($suggestions,substr( $sp[$x], 8,-3 ) );
    //echo "<br>";

$suggestions=$suggestions."<option value=\"".$q_prefix.substr( $sp[$x], 8,-3 )."\">".$q_prefix.substr( $sp[$x], 8,-3 )."</option>" ;
   }
}
suggestions."</select>";


echo $suggestions;




?>