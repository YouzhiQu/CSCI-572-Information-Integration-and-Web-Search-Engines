<?php
ini_set('memory_limit', '1024M');
include 'SpellCorrector.php';
header('Content-Type:text/html; charset=utf-8');
$limit = 10;
$div=false;
$correct = "";
$correct1="";
$output = "";
$query= isset($_REQUEST['q'])?$_REQUEST['q']:false;
$results = false;
if($query){
    require_once('solr-php-client/Apache/Solr/Service.php');
    $choice = isset($_REQUEST['algorithm'])? $_REQUEST['algorithm'] : "lucene";
    $solr = new Apache_Solr_Service('localhost', 8983, '/solr/youzhi/');
    if(!$solr->ping()) {
            echo 'Solr service is not available';
    }
    if(get_magic_quotes_gpc() == 1){
        $query = stripslashes($query);
    }
    try{
        if(!isset($_GET['algorithm']))$_GET['algorithm']="lucene";
        if($_GET['algorithm'] == "lucene"){
            $param = array('sort'=>'');
            // $results = $solr->search($query, 0, $limit);
        }else{
            $param = array('sort'=>'pageRankFile desc');
            // $results = $solr->search($query, 0, $limit, $param);
        }

        $word = explode(" ",$query);
        $encode_query = str_replace(" ","+",$query);
        $spell = $word[sizeof($word)-1];
        for($i=0;$i<sizeOf($word);$i++){
          ini_set('memory_limit',-1);
          ini_set('max_execution_time', 300);
          $che = SpellCorrector::correct($word[$i]);
          if($correct!="")
            $correct = $correct."+".trim($che);
          else{
            $correct = trim($che);
          }
            $correct1 = $correct1." ".trim($che);
        }
        $correct1 = str_replace("+"," ",$correct);
        $div=false;
        if(strtolower($query)==strtolower($correct1)){
          $results = $solr->search($query, 0, $limit, $param);
        }
        else {
          if(isset($_REQUEST['custom'])) {
            $results = $solr->search($query, 0, $limit, $param);
            $div=false;
          }else{
            $div=true;
            $results = $solr->search($correct, 0, $limit, $param);
          }
          $link = "http://localhost/autocomplete.php?q=$correct&algorithm=$choice";
          $origin = "http://localhost/autocomplete.php?q=$encode_query&algorithm=$choice&custom=ture";
          $output = "<div class='h3'>Showing results for: <a href='$link'>$correct1</a></div>"
                    ."<div class='h3'>Search instead for: <a href='$origin'>$query</a></div>";

        }
    }
    catch(Exception $e){
        die("<html><head><title>SEARCH EXCEPTION</title></head><body><pre>{$e->__toString()}</pre></body></html>");
    }
}
?>
<html>
<head>
    <title> Indexing the Web Using Solr </title>
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">

    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>

    <!-- Latest compiled JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script> 
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <link rel="stylesheet" href="http://code.jquery.com/ui/1.12.1/themes/smoothness/jquery-ui.css" rel="Stylesheet"></link>
    <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
    <script src="http://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
     
    <script>
        $(document).ready(function(){
          if(localStorage.selected) {
            $('#' + localStorage.selected ).attr('checked', true);
          }
          $('.inputabs').click(function(){
            localStorage.setItem("selected", this.id);
          });
        });
        $(function() {
              var URL_Start = "http://localhost:8983/solr/youzhi/suggest?q=";
              var URL_End = "&wt=json&indent=true";
              var count=0;
              var tags = [];
              $("#q").autocomplete({
                source : function(request, response) {
                  var correct="",before="";
                  var query = $("#q").val().toLowerCase();
                  var character_count = query.length - (query.match(/ /g) || []).length;
                  var space =  query.lastIndexOf(' ');
                  if(query.length-1>space && space!=-1){
                    correct=query.substr(space+1);
                    before = query.substr(0,space);
                  }
                  else{
                    correct=query.substr(0);
                  }
                  var URL = URL_Start + correct+ URL_End;
                  $.ajax({
                  url : URL,
                  success : function(data) {
                   var js =data.suggest.suggest;
                   var docs = JSON.stringify(js);
                   var jsonData = JSON.parse(docs);
                   var result =jsonData[correct].suggestions;
                   var j=0;
                   var stem =[];
                   console.log(result);
                   for(var i=0;i<5 && j<result.length;i++,j++){
                     for(var k=0;k<i && i>0;k++){
                       if(tags[k].indexOf(result[j].term) >=0){
                         i--;
                         continue;
                       }
                     }
                     if(result[j].term.indexOf('.')>=0 || result[j].term.indexOf('_')>=0)
                     {
                       i--;
                       continue;
                     }
                     var s =(result[j].term);
                     if(stem.length == 5)
                       break;
                     if(stem.indexOf(s) == -1)
                     {
                       stem.push(s);
                       if(before==""){
                         tags[i]=s;
                       }
                       else
                       {
                         tags[i] = before+" ";
                         tags[i]+=s;
                       }
                     }
                   }
                   console.log(tags);
                   response(tags);
                 },
                 dataType : 'jsonp',
                 jsonp : 'json.wrf'
               });
               },
               minLength : 1
             })
            });
    </script>
</head>
<body>
<div class="container d-flex justify-content-center">
  <h1 class="text-center">Lucene VS Google PageRank Algorithm</h1><br/>
    <form class="text-center h4"  accept-charset="utf-8" method="get">
        <input  id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8');?>"
                list="searchresults" placeholder="Input query words" autocomplete="off"/>
                <datalist id="searchresults"></datalist>
                <input type="hidden" name="spellcheck" id="spellcheck" value="false">
        <br/><?php if ($div){echo $output;}?><br/>
        <input class="inputabs" id="solr" type="radio" name="algorithm" value="lucene" /> Solr
        <input class="inputabs" id="google" type="radio" name="algorithm" value="pagerank" /> PageRank <br/><br/>
        <input type="submit" />
    </form>
</div>
<?php

$count =0;
$prev="";
$arrayFromCSV =  array_map('str_getcsv', file('URLtoHTML_guardian_news.csv'));
if ($results)
{
  $total = (int) $results->response->numFound;
  $start = min(1, $total);
  $end = min($limit, $total);
  echo " <div class='container d-flex justify-content-center' <p class='text-center'>Results $start -  $end of $total :</p> <ol>";
  foreach ($results->response->docs as $doc)
  {  
    $id = $doc->id;
    $title = $doc->title;
    $desc = $doc->description;
    if($title=="" ||$title==null){
     $title = $doc->dc_title;
     if($title=="" ||$title==null)
       $title="N/A";
   }
   $originId = $id;
   $id = str_replace("/home/youzhi/Desktop/cs572/guardiannews/","",$id);
   foreach($arrayFromCSV as $row1)
   {
    if($id==$row1[0])
    {
      $url = $row1[1];
      break;
    }
  }

  if(!isset($_REQUEST['custom'])){
    $ary = explode(" ", $correct1);
  }else{
    $ary = explode(" ", $query);
  }

  // $searchfor = $_GET["q"];
  // $ary = explode(" ",$searchfor);
  $count = 0;
  $max = 0;
  $finalSnippet = "";
    
   




  $HtmlText = substr($id,0,strlen($id)-5);
  $html_to_text_files_dir = "/home/youzhi/Desktop/cs572/parsedData/";
  $file_name = $html_to_text_files_dir . $HtmlText;
  $file = fopen($file_name,"r");
  
  while(! feof($file))
  {
    $snippet = fgets($file);

    $elementlower = strtolower($snippet);
    foreach($ary as $wd)
    {
      $wd = strtolower($wd);
      if (strpos($elementlower, $wd) !== false) 
      {
        $count = $count+1;
      }
    }
    if($max<$count)
    {
      $finalSnippet = $snippet;
      $max = $count;
    }
    else if($max==$count && $count>0)
    {
      if(strlen($finalSnippet)<strlen($snippet))
      {
        $finalSnippet = $snippet;
        $max = $count;
      }
    }
    $count = 0;
  }
  $pos = 0;
  $wd = "";
  foreach ($ary as $wd) {
    if (strpos(strtolower($finalSnippet), strtolower($wd)) !== false) 
    {
      $pos = strpos(strtolower($finalSnippet), strtolower($wd));
      break;
    }
  }
  $start = 0;
  if($pos>80)
  {
    $start = $pos - 80; 
  }
  else
  {
    $start = 0;
  }
  $end = $start + 160;
  if(strlen($finalSnippet)<$end)
  {
    $end = strlen($finalSnippet)-1;
    $post1 = "";
  }
  else
  {
    $post1 = "...";
  }
  
  if(strlen($finalSnippet)>160)
  {
    if($start>0)
      $pre = "...";
    else
      $pre = "";
    
    $finalSnippet = $pre . substr($finalSnippet,$start,$end-$start+1) . $post1;
  }
  $start_delim="/(?=.*?\b";
  $end_delim="\b)";
  $isNA = false;

  foreach ($ary as $item) {
    $text='';
    $text=$start_delim.$item.$end_delim.'^.*$/i';
    if(preg_match($text, $finalSnippet)>0){
      $finalSnippet = str_ireplace($item, "<strong>".$item."</strong>",$finalSnippet);
      $isNA = true;
    }
  }
  if($isNA==false) {
    $finalSnippet = "N/A";
  }
  
  fclose($file);
  unset($row1);
  error_reporting(E_ALL ^ E_NOTICE);  
  // echo "<li><a href=' ' target='_blank'>$title</a></br>
  // <a href='$url' target='_blank'>$url</a></br>
  // <b>Description:</b> $desc<br/>
  // <b>Snippet:</b>$finalSnippet<br/>
  // <b>ID: </b> $id2</li></br></br>";
  // array_push($stack,$id2);
  ?>
  <li>
    <table class="table table-striped table-hover">
      <tr>
        <th><?php echo htmlspecialchars("TITLE", ENT_NOQUOTES, 'utf-8'); ?></th>
        <td><?php echo "<a href = '{$url}' style='text-decoration:none'><st><b>".$title."</b></st></a>" ?></td>

      </tr>
      <tr>
        <th><?php echo htmlspecialchars("URL", ENT_NOQUOTES, 'utf-8'); ?></th>
        <td><?php echo "<a href = '{$url}' style='text-decoration:none'><st>".$url."</st></a>" ?></td>
      </tr>
      <tr>
        <th><?php echo htmlspecialchars("ID", ENT_NOQUOTES, 'utf-8'); ?></th>
        <td><?php echo htmlspecialchars($originId, ENT_NOQUOTES, 'utf-8'); ?></td>
      </tr>

      <tr>
        <th><?php echo htmlspecialchars("SNIPPET", ENT_NOQUOTES, 'utf-8'); ?></th>
        <td><?php
        if($finalSnippet == "N/A"){
          echo htmlspecialchars($finalSnippet, ENT_NOQUOTES, 'utf-8');
        }else{
          echo "...".$finalSnippet."...";
        }
        ?></td>
      </tr>
    </table>
  </li>
  <?php
  }
echo "</ol></div>";
}
?>

</body>
</html>