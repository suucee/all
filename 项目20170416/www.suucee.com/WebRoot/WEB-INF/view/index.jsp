<!DOCTYPE html>
<html>

	<head>
		<title>fxants</title>
		<link rel="stylesheet" href="../css/bootstrap.min.css" />
		<link href="../css/mycss.css" rel="stylesheet">
		<meta Charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1">
	</head>

	<body>
	
	
		<!--sidebar start-->
		<!--sidebar start-->
		
		
		<div id="ourphone" class="sidebar">
			<span class="sidebarleft">
				<img class="sidbarimg" src="../img/sidebarimg/phone.png"/>
				<p>联系<br>我们</p>
			</span>
			<span class="sidebarright">
				<p>400-2342-231</p>
			</span>
		</div>
		
		<div id="ourQQ" class="sidebar">
			<span class="sidebarleft">
				<img class="sidbarimg" src="../img/sidebarimg/qq.png"/>
				<p>Q Q<br>客服</p>
			</span>
			<span class="sidebarright">
				<p>245556547</p>
			</span>
		</div>
		
		<div id="ourweixin" class="sidebar">
			<span class="sidebarleft">
				<img class="sidbarimg" src="../img/sidebarimg/weixin.png"/>
				<p>官方<br>微信</p>
			</span>
			<span class="sidebarright">
				<img  src="../img/sidebarimg/qr.jpg"/>
			</span>
		</div>
		<!--sidebar end-->
		
		<!--top_header start-->
		<div class="container-fluid" id="top_header">
			<div class="container">
				<div class="row">
					<div class="col-xs-12 col-md-6 pushleft">
						<a href="#">
							<span class="glyphicon glyphicon-earphone">
							<p class="hidden-xs">400-2342-231</p></span>
						</a>
						<span id="langtitle"><a href="#">中文</a>
							<span id="langbody"><a href="#">English</a></span>
						</span>
						<span>
								<a href="#">通告
								<img src="../img/icon_new.png" /></a>
							</span>
						</a>
					</div>
					<div class="col-xs-12 col-md-6 pushright">
						<span><button id="denglu" class="btn">登陆</button></span>
						<span><button id="sqdlzh" class="btn">申请登录账户</button></span>
						<span><button id="sqmnzh" class="btn">申请模拟账户</button></span>
					</div>
				</div>
			</div>
		</div>
		<!--top_header end-->

		<!--navbar start-->
		<div class="container-fluid" id="header-menu">
			<div class="container">
				<div class="row" id="navbar">
					<div class="col-xs-12 col-md-2  " id="logoimg">
						<a><img class="img-responsive" src="../img/logo.jpg"></a>
					</div>

					<div class="col-xs-6 col-sm-12 col-md-8 " id="mynav">
						<nav class="navbar navbar-default" role="navigation">
							<div class="navbar-header">
								<button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#example-navbar-collapse">
         							<span class="sr-only">切换导航</span>
							        <span class="icon-bar"></span>
							        <span class="icon-bar"></span>
							        <span class="icon-bar"></span>
							    </button>
								<a class="navbar-brand" href="#">
									<p class="visible-xs"></p>
								</a>
							</div>
							<div class="collapse navbar-collapse" id="example-navbar-collapse">
								<ul class="nav navbar-nav">
									
									<li class="head_div">
										<a class="navItem" href="whsc.do">交易与产品</a>
										<ul class="head_ul">
											<li>
												<a class="footer_div_1" href="whsc.do">外汇市场</a>
												<a class="footer_div_1" href="whjs.do">贵金属市场</a>
												<a class="footer_div_1" href="jrfy.do">标准交易账户</a>
												<a class="footer_div_1" href="mnjyzh.do">迷你交易账户</a>
											</li>
										</ul>
									</li>
									<li class="head_div">
										<a class="navItem" href="xghszh.do">产品服务</a>
										<ul class="head_ul">
											<li>
												<a class="footer_div_1" href="xghszh.do">香港恒生指数</a>
												<a class="footer_div_1" href="hjby.do">黄金白银</a>
												<a class="footer_div_1" href="gjyy.do">国际原油</a>
												<a class="footer_div_1" href="whhb.do">外汇货币</a>
											</li>
										</ul>
									</li>
									<li class="head_div">
										<a class="navItem" href="zcmnzh.do">客户中心</a>
										<ul class="head_ul">
											<li>
												<a class="footer_div_1" href="zcmnzh.do">注册模拟账户</a>
												<a class="footer_div_1" href="kszszh.do">开设真实账户</a>
												<a class="footer_div_1" href="crzj.do">存入资金</a>
												<a class="footer_div_1" href="tkjzz.do">提款及转账</a>
											</li>
										</ul>
									</li>
									<li class="head_div">
										<a class="navItem" href="MT4dnb.do">下载中心</a>
										<ul class="head_ul">
											<li>
												<a class="footer_div_1" href="MT4dnb.do">MT4电脑版</a>
												<a class="footer_div_1" href="MT4sjb.do">MT4手机版</a>
												<a class="footer_div_1" href="wjxz.do">文件下载</a>
											</li>
										</ul>
									</li>
									<li class="head_div">
										<a class="navItem" href="gsjj.do">关于我们</a>
										<ul class="head_ul">
											<li>
												<a class="footer_div_1" href="gsjj.do">公司简介</a>
												<a class="footer_div_1" href="hzhb.do">合作伙伴</a>
												<a class="footer_div_1" href="xwfb.do">新闻发布</a>
											</li>
										</ul>
									</li>

								</ul>
							</div>
						</nav>
					</div>

					 <div class="col-xs-6 col-sm-12 col-md-2 "  style="display: none" id="search">
						<form role="form">
							<div id="searchbar">
								<input class="form-control" type="text" placeholder="搜索..." id="searchtext">
								<input type="submit" value="  " id="searchsubmit">
							</div>
						</form>

					</div> 
				</div>
			</div>
		</div>
		<!--navbar end-->

		<!--carousel start-->
		<div id="carousel-example-generic" class="carousel slide" data-ride="carousel">
			<!-- Indicators -->
			<ol class="carousel-indicators">
				<li data-target="#carousel-example-generic" data-slide-to="0" class="active"></li>
				<li data-target="#carousel-example-generic" data-slide-to="1"></li>
				<li data-target="#carousel-example-generic" data-slide-to="2"></li>
			</ol>

			<!-- Wrapper for slides -->
			<div class="carousel-inner" role="listbox">
				<div class="item active">
					<img src="../img/carousel/t1.png" alt="...">
					<div class="carousel-caption">
						...
					</div>
				</div>

				<div class="item">
					<img src="../img/carousel/t2.png" alt="...">
					<div class="carousel-caption">
						...
					</div>
				</div>

				<div class="item">
					<img src="../img/carousel/4.jpg" alt="...">
					<div class="carousel-caption">
						...
					</div>
				</div>
			</div>

		</div>
		<!--carousel end-->

		<!--carousel-bottom start-->
		<div class="row " id="carousel-buttom" style="background-color: #2F67A2;">
			<div class="container">
				<div style="width:19%;" class="col-xs-6 col-sm-4 col-md-2 col-md-offset-1 carousel-buttom-border">
					<span class="num">45</span><span  class="text">种外汇、指数以及商品期货和贵金属</span>

				</div>
				<div class="col-xs-6 col-sm-4 col-md-2 carousel-buttom-border">
					<span class="num">400:1</span><span class="text">杠杆</span>
				</div>
				<div class="col-xs-6 col-sm-4 col-md-3 carousel-buttom-border">
					<span class="num">$100</span><span class="text">最低入金<br>存款</span>
				</div>
				<div class="col-xs-6 col-sm-4 col-md-2 carousel-buttom-border">
					<span class="num">$0</span><span class="text">免费开户</span>
				</div>
				<div class="col-xs-6 col-sm-4 col-md-2">
					<span class="num">9</span><span class="text">种连接<br>MT4的方法</span>
				</div>
			</div>
		</div>
		<!--carousel-bottom end-->

		<!--slider-link start-->
		<div class="slider_link">
			<div class="slider_link_inner">
				<h3>你想成为下一个索罗斯吗？</h3>
				<a class="link_open_account" href="/huodong/competition/competition-regiser/">参加实盘大赛</a>
				<a class="link_trial_demo" target="_blank" href="https://myaccount.vantagefx.cn/activity/to_demoCompetition">参加模拟大赛</a>
				<div class="clear"></div>
			</div>
		</div>
		<!--slider-link end-->

		<!--content start-->
		<div class="container">
			<div class="row con1">
				<div class="tend" style="float: left;">
					<div class="title col-xs-12">
						<p>实时价格</p>
					</div>
					<div class="tend-pic">						
               	 	<iframe id="html-page" style=" border:none; width: 610px;height: 474px; "  src="data_parameters.html"></iframe> 
              
						<!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~引入另一个html文件失败-->
					</div>
				</div>
				<!--con1-left end-->
				<div class="process ">
					<div class="title found col-xs-12">
						<p >开户流程</p>
					</div>

					<div class="online col-xs-12">
						<div class="col-xs-5">
							<div class="on-pic"></div>
							<p class="pro-text1">在线提交申请</p>
						</div>
						<p class="pro-text2 col-xs-7">点击“申请真实账户”按钮，进入注册账户界面</p>
					</div>

					<div class="online col-xs-12">
						<div class="col-xs-5">
							<div class="apply-pic"></div>
							<p class="pro-text1">提交资料审核</p>
						</div>
						<p class="pro-text2 col-xs-7">提交相关信息资料，我们会在极短时间内为您审核，通过</p>
					</div>

					<div class="online col-xs-12">
						<div class="col-xs-5">
							<div class="cash-pic"></div>
							<p class="pro-text1">注入首笔资金</p>
						</div>
						<p class="pro-text2 col-xs-7">使用信用卡或借记卡，向您的账户注入资金，进行交易</p>
					</div>

					<div class="apply-icon">
						<div class="col-xs-12"><button id="sqdlzh" class="btn">申请登录账户</button></div>
						<div class="col-xs-12"><button id="sqmnzh" class="btn">申请模拟账户</button></div>
					</div>

					<p class="tip">TIP:我们提供$20.000虚拟资金，让客户可以体验平台进行交易</p>
				</div>
				<!--con1-right end-->
			</div>
			<!--content-con1 end-->

			<!--content-con2 start-->
			<div class="con2 row">
				<div class="adv-produce">
					<div class="title col-xs-12">
						<p>产品优势</p>
					</div>

					<div class="produce col-xs-12 col-md-4">
						<img src="../img/4.jpg" />
						<!--<div class="produce-pic1 col-xs-12">
							<img src="../img/4.jpg" />
						</div>-->

						<div class="product-inner">
							<h3>强大的交易平台</h3>
							<h4>全球最强大，使用人数最多的交易平台 — — MT4</h4>

							<div class="produce-list" style="margin-top: 30px;">
								<div class="right-icon"></div>
								<div class="produce-text">自动交易模式</div>
							</div>
							<br>
							<div class="produce-list" style="margin-top: -10px;">
								<div class="right-icon"></div>
								<p class="produce-text">高级图表功能</p>
							</div>
							<div class="produce-list">
								<div class="right-icon"></div>
								<p class="produce-text">多种客户端下载，随时随地可以交易</p>
							</div>
						</div>
					</div>
					
					
					<div class="produce col-xs-12 col-md-3" id="produce2">
					<div class="produce-pic2"></div>
						<div class="product-inner" >
						<h3>丰富的交易产品</h3>
						<h4>我们提供88种产品交易，满足不同客户的需求</h4>
						<div class="produce-list" style="margin-top: 30px;">
							<div class="right-icon"></div>
							<p class="produce-text">55种货币对</p>
						</div>
						<div class="produce-list">
							<div class="right-icon"></div>
							<p class="produce-text">贵金属原油交易</p>
						</div>
						<div class="produce-list">
							<div class="right-icon"></div>
							<p class="produce-text">多种股票指数交易</p>
						</div>
						</div>
					</div>

					<div class="produce col-xs-12 col-md-4" id="produce3">
						<div class="produce-pic3"></div>
						<div class="product-inner">
							<h3>最佳的交易环境</h3>
							<h4>所有订单直接进入银行间市场，订单立即执行，小于1秒</h4>
							<div class="produce-list" style="margin-top: 30px;">
								<div class="right-icon"></div>
								<p class="produce-text">交易过程透明、公平</p>
							</div>
							<div class="produce-list">
								<div class="right-icon"></div>
								<p class="produce-text">点差低至0.1</p>
							</div>
							<div class="produce-list">
								<div class="right-icon"></div>
								<p class="produce-text">1:200的杠杆</p>
							</div>
						</div>
					</div>
				</div>
				<div class="clear"></div>
			</div>
			<!--content-con2 end-->

			<!--content-con3 start-->
			<div class="con3 row">
				<div class="contact">
					<div id="title_3" class="title col-xs-12">
						<p>联系我们</p>
					</div>

					<div class="scan col-xs-4">
						<div class="code"><img src="../img/2维码.jpg"/></div>
						<p style="margin-top: 20px; text-align:center;font-size:14px;">扫二维码<br>
						关注我们官方微信</p>
					</div>
					<div class="tel col-xs-8">
						<p>我们的客户支持团队在北京时间周一至周五早8点至晚10点随时准备为您服务，若有疑问，请及时与我们联系。</p>
						<div class="number" >
							<p>QQ:2344565757</p>
							<p>客服：400-9898-3434</p>
							<p>邮箱：400-9898-3434</p>
						</div>
					</div>
				</div>
				<!--content-thirdline-left end-->
				<div class="news col-xs-12 col-md-6">
					<div id="title_4" class="title col-xs-12">
						<p>新闻快讯</p>
						<div class="add"></div>
						<div class="clear"></div>
					</div>
					<div class="new col-xs-7">
						<ul>
							<li>
								<div class="dot"></div>
								<a title="" target="_blank" href="xwfb/information/251.do">券商年报抖料：证金专户浮亏5% 两融业务现坏账</a>
							</li>
							<li>
								<div class="dot"></div>
								<a title="" target="_blank" href="xwfb/information/250.do">梧桐树概念股已增至11只 外汇局大举入市答案在这儿</a>
							</li>
							<li>
								<div class="dot"></div>
								<a title="" target="_blank" href="xwfb/information/249.do"> 4月，趁反弹调整投资组合</a>
							</li>
							<li>
								<div class="dot"></div>
								<a title="" target="_blank" href="xwfb/information/248.do">央行：降准会导致人民币贬值、资本外流和外储下降</a>
							</li>
							<li>
								<div class="dot"></div>
								<a title="" target="_blank" href="xwfb/information/247.do">哈继铭：M2每年14%的增长 人民币不可能不贬值</a>
							</li>
							<li>
								<div class="dot"></div>
								<a title="" target="_blank" href="xwfb/information/246.do">豪掷千亿美金！中国企业为何要在海外买买买？</a>
							</li>
							<li>
								<div class="dot"></div>
								<a title="" target="_blank" href="xwfb/information/245.do">习近平会见朴槿惠 就朝鲜半岛形势交换看法</a>
							</li>
						</ul>
					</div>
					<div class="time col-xs-5">
						<p>2016-04-01 12</p>
						<p>2016-04-01 11</p>
						<p>2016-04-01 11</p>
						<p>2016-04-01 11</p>
						<p>2016-04-01 11</p>
						<p>2016-04-01 11</p>
						<p>2016-04-01 11</p>
					</div>
				</div>
				<!--content-thirdline-right end-->

			</div>
			<!--content-con3 end-->

			<div class="con4 row">
				<div class="title col-xs-12">
					<p>合作银行</p>
				</div>
				
				<div class="bank col-xs-2 col-xs-offset-1"><a href="#"> <img src="../img/10.jpg"/></a></div>
				<div class="bank col-xs-2"><a href="#"> <img src="../img/11.jpg"/></a></div>
				<div class="bank col-xs-2"><a href="#"> <img src="../img/12.jpg"/></a></div>
				<div class="bank col-xs-2"><a href="#"> <img src="../img/13.jpg"/></a></div>
				<div class="bank col-xs-2"><a href="#"> <img src="../img/14.jpg"/></a></div>
				
			</div>
		</div>

		<!--content end-->

		<!--footer start-->
		<div class="navfooter row">
		<div class="footer">
		<div class="col-xs-12  col-md-2 col-md-offset-1">
			<a class="navItem" href="whsc.do">交易与产品</a>
			
			<ul class="footer_ul">
				<li><a class="footer_div_1" href="whsc.do">外汇市场</a></li>
				<li><a class="footer_div_1" href="whjs.do">贵金属市场</a></li>
				<li><a class="footer_div_1" href="jrfy.do">标准交易账户</a></li>
				<li><a class="footer_div_1" href="mnjyzh.do">迷你交易账户</a></li>				
			</ul>
		</div>
		
			<div class="col-xs-12 colrow col-md-2">
				<a class="navItem" href="xghszh.do">产品服务</a>
				<ul class="footer_ul">
					<li><a class="footer_div_1" href="xghszh.do">香港恒生指数</a></li>
					<li><a class="footer_div_1" href="hjby.do">黄金白银</a></li>
					<li><a class="footer_div_1" href="gjyy.do">国际原油</a></li>
					<li><a class="footer_div_1" href="whhb.do">外汇货币</a></li>					
				</ul>			
			</div>
			
			<div class="col-xs-12 colrow col-md-2">
				<a class="navItem" href="zcmnzh.do">客户中心</a>
				<ul class="footer_ul">
					<li><a class="footer_div_1" href="zcmnzh.do">注册模拟账户</a></li>
					<li><a class="footer_div_1" href="kszszh.do">开设真实账户</a></li>
					<li><a class="footer_div_1" href="crzj.do">存入资金</a></li>
					<li><a class="footer_div_1" href="tkjzz.do">提款及转账</a></li>					
				</ul>
			</div>
			
			<div class="col-xs-12 colrow col-md-2">
				<a class="navItem" href="MT4dnb.do">下载中心</a>
				<ul class="footer_ul">
					<li><a class="footer_div_1" href="MT4dnb.do">MT4电脑版</a></li>
					<li><a class="footer_div_1" href="MT4sjb.do">MT4手机版</a></li>
					<li><a class="footer_div_1" href="wjxz.do">文件下载</a></li>					
				</ul>
			</div>
			
			<div class="col-xs-12 colrow col-md-2">
				<a class="navItem" href="gsjj.do">关于我们</a>
				<ul class="footer_ul">
					<li><a class="footer_div_1" href="gsjj.do">公司简介</a></li>
					<li><a class="footer_div_1" href="hzhb.do">合作伙伴</a></li>
					<li><a class="footer_div_1" href="xwfb.do">新闻发布</a></li>					
				</ul>
			</div>
			</div>
		</div>
		<!--footer-firstline end-->

		<div class="footer_wei row">
			<ul>
				<li><a>金融服务指南</a>|<a>产品披露申明</a>|<a>交易条款</a></li>
				<li><a>@xxxx公司</a>|<a>ACN 公司号码 117 055 703</a>|<a>川ICP备xxxxxxx号-1</a></li>
			</ul>
		</div>
		<!--footer-secondline end-->
		<!--footer end-->
	</body>

	<script type="text/javascript" src="../js/jquery-2.2.1.min.js"></script>
	<script type="text/javascript" src="../bootstrap-3.3.5-dist/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="../js/myjs.js"></script>

</html>