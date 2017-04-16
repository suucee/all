-- phpMyAdmin SQL Dump
-- version 4.6.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: 2017-04-16 06:30:42
-- 服务器版本： 5.7.14
-- PHP Version: 5.6.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `www_suucee_com`
--

-- --------------------------------------------------------

--
-- 表的结构 `admins`
--

CREATE TABLE `admins` (
  `id` int(10) UNSIGNED NOT NULL,
  `role` varchar(50) NOT NULL COMMENT '状态',
  `is_disabled` tinyint(1) NOT NULL DEFAULT '0' COMMENT '禁用flag',
  `account` varchar(50) NOT NULL,
  `password` varchar(32) NOT NULL,
  `operation_password` varchar(32) NOT NULL COMMENT '操作密码',
  `salty` varchar(6) NOT NULL COMMENT '密码盐',
  `show_name` varchar(50) NOT NULL COMMENT '显示名称',
  `creat_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `last_logon_time` timestamp NULL DEFAULT NULL COMMENT '上次登录时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='管理员表';

--
-- 转存表中的数据 `admins`
--

INSERT INTO `admins` (`id`, `role`, `is_disabled`, `account`, `password`, `operation_password`, `salty`, `show_name`, `creat_time`, `last_logon_time`) VALUES
(1, 'ComplianceOfficer', 0, 'hegui', '909ef0ea70311b05c8c6707b2cde3592', '654321akjd83', 'akjd83', '测试合规', '2016-03-15 10:34:28', NULL),
(2, 'Webmaster', 0, 'zhanzhang', '951edffa59d2f561383e94015856b785', '123456akjd83', 'akjd83', '站长管理', '2016-03-21 16:00:00', NULL);

-- --------------------------------------------------------

--
-- 表的结构 `admin__logs`
--

CREATE TABLE `admin__logs` (
  `id` int(10) UNSIGNED NOT NULL,
  `admin_id` int(10) UNSIGNED NOT NULL,
  `creat_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_address` varchar(50) NOT NULL COMMENT 'IP地址',
  `action` varchar(50) NOT NULL COMMENT '动作',
  `description` text NOT NULL COMMENT '描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `announcement`
--

CREATE TABLE `announcement` (
  `id` int(11) NOT NULL,
  `content` longtext,
  `create_time` datetime NOT NULL,
  `display` bit(1) NOT NULL DEFAULT b'1',
  `modify_time` datetime NOT NULL,
  `publish_time` datetime DEFAULT NULL,
  `sort` int(11) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `top` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否置顶：1=true=置顶，0=false=不置顶',
  `admins_id` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `archives`
--

CREATE TABLE `archives` (
  `id` int(10) UNSIGNED NOT NULL,
  `type` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `archives`
--

INSERT INTO `archives` (`id`, `type`) VALUES
(215, 'column'),
(216, 'column'),
(217, 'column'),
(218, 'column'),
(219, 'column'),
(220, 'column'),
(221, 'column'),
(222, 'column'),
(223, 'column'),
(224, 'column'),
(225, 'column'),
(226, 'column'),
(227, 'column'),
(228, 'column'),
(229, 'column'),
(230, 'column'),
(231, 'column'),
(232, 'column'),
(233, 'column'),
(234, 'column'),
(235, 'column'),
(236, 'column'),
(237, 'column'),
(238, 'column'),
(239, 'column'),
(240, 'column'),
(241, 'column'),
(242, 'column'),
(243, 'article'),
(244, 'article'),
(245, 'article'),
(246, 'article'),
(247, 'article'),
(248, 'article'),
(249, 'article'),
(250, 'article'),
(251, 'article'),
(252, 'column');

-- --------------------------------------------------------

--
-- 表的结构 `attachments`
--

CREATE TABLE `attachments` (
  `id` int(10) UNSIGNED NOT NULL,
  `is_image` tinyint(1) DEFAULT NULL,
  `type` varchar(50) NOT NULL,
  `owner_type` varchar(50) NOT NULL,
  `owner_id` int(10) UNSIGNED NOT NULL,
  `name` varchar(50) NOT NULL,
  `path` varchar(260) NOT NULL,
  `filesize` bigint(20) UNSIGNED NOT NULL DEFAULT '0',
  `creat_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sort_num` int(11) NOT NULL DEFAULT '50',
  `user_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `attachments`
--

INSERT INTO `attachments` (`id`, `is_image`, `type`, `owner_type`, `owner_id`, `name`, `path`, `filesize`, `creat_time`, `sort_num`, `user_id`) VALUES
(203, 0, 'image/jpeg', '', 0, '51601f72-be78-42cd-aaed-bb601b9313f0.jpg', '/userProfile/201603/30/51601f72-be78-42cd-aaed-bb601b9313f0.jpg', 617382, '2016-03-30 10:08:03', 50, 43),
(204, 0, 'image/jpeg', '', 0, '9bc421dc-ce98-43db-8a71-7b7c1f92ae51.jpg', '/userProfile/201603/30/9bc421dc-ce98-43db-8a71-7b7c1f92ae51.jpg', 331131, '2016-03-30 10:08:08', 50, 43),
(205, 0, 'image/jpeg', '', 0, 'fc4419bf-ef52-42b6-b6cd-44ffa29a1aec.jpg', '/userProfile/201603/30/fc4419bf-ef52-42b6-b6cd-44ffa29a1aec.jpg', 380827, '2016-03-30 10:08:14', 50, 43),
(206, 0, 'image/jpeg', '', 0, '72a0134c-a1f1-4c0d-bdf9-d5c19d461c34.jpg', '/userProfile/201603/30/72a0134c-a1f1-4c0d-bdf9-d5c19d461c34.jpg', 617382, '2016-03-30 10:08:19', 50, 43),
(207, 0, 'image/jpeg', '', 0, 'df1a92dc-8ee4-44aa-b53b-6f3e6d2dc1c0.jpg', '/userProfile/201603/30/df1a92dc-8ee4-44aa-b53b-6f3e6d2dc1c0.jpg', 331131, '2016-03-30 10:08:23', 50, 43),
(211, 0, 'image/jpeg', '', 0, 'd5a962b7-adfa-41d4-b367-40f2420795f5.jpg', '/userProfile/201603/30/d5a962b7-adfa-41d4-b367-40f2420795f5.jpg', 39200, '2016-03-30 10:11:17', 50, 43),
(212, 0, 'image/jpeg', '', 0, 'abfa6443-7d4b-42fc-8336-f5fad3255d71.jpg', '/userProfile/201603/30/abfa6443-7d4b-42fc-8336-f5fad3255d71.jpg', 39200, '2016-03-30 10:11:44', 50, 43);

-- --------------------------------------------------------

--
-- 表的结构 `cdd_checks`
--

CREATE TABLE `cdd_checks` (
  `id` int(10) UNSIGNED NOT NULL,
  `admin_id` int(10) UNSIGNED NOT NULL,
  `user_id` int(10) UNSIGNED DEFAULT NULL,
  `deposit_id` int(10) UNSIGNED DEFAULT NULL,
  `withdrawal_id` int(10) UNSIGNED DEFAULT NULL,
  `result` varchar(20) NOT NULL COMMENT '状态(未决PENDING、通过ACCEPTED、拒绝REJECTED)',
  `tag` varchar(50) NOT NULL COMMENT '标记',
  `comment` longtext NOT NULL COMMENT '批注',
  `snapshot` longtext NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `reminder_timestamp` timestamp NULL DEFAULT NULL COMMENT '提醒時間',
  `url` varchar(200) NOT NULL,
  `user_bank_account_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `columns`
--

CREATE TABLE `columns` (
  `id` int(10) UNSIGNED NOT NULL,
  `up_id` int(10) UNSIGNED DEFAULT NULL COMMENT '上级栏目ID',
  `alias` varchar(100) NOT NULL,
  `name` varchar(50) NOT NULL COMMENT '栏目名称',
  `content_type` varchar(50) NOT NULL COMMENT '内容类型',
  `sort_num` int(11) NOT NULL DEFAULT '50' COMMENT '排序数',
  `url` varchar(200) NOT NULL DEFAULT '',
  `body` longtext NOT NULL,
  `seo_title` varchar(80) NOT NULL,
  `seo_keywords` varchar(200) NOT NULL,
  `seo_description` varchar(200) NOT NULL,
  `creat_time` datetime NOT NULL,
  `channe_template` varchar(100) NOT NULL,
  `content_template` varchar(100) NOT NULL,
  `list_template` varchar(100) NOT NULL,
  `state` bit(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `columns`
--

INSERT INTO `columns` (`id`, `up_id`, `alias`, `name`, `content_type`, `sort_num`, `url`, `body`, `seo_title`, `seo_keywords`, `seo_description`, `creat_time`, `channe_template`, `content_template`, `list_template`, `state`) VALUES
(215, NULL, 'index', '首页', 'information', 50, 'index.do', '', '', '', '', '2016-03-17 13:54:19', '/index', '', '', b'0'),
(217, NULL, 'jyycp', '交易与产品', 'information', 50, 'whsc.do', '', '', '', '', '2016-04-06 15:03:37', '', '', '', b'0'),
(218, 217, 'whsc', '外汇市场', 'information', 50, 'whsc.do', '', '', '', '', '2016-04-06 15:04:11', '/information_2_col', '', '', b'0'),
(220, 217, 'gjssc', '贵金属市场', 'information', 50, 'whjs.do', '', '', '', '', '2016-04-06 15:04:31', '/information_2_col', '', '', b'0'),
(221, 217, 'jrfy', '标准交易账户', 'information', 50, 'jrfy.do', '', '', '', '', '2016-04-06 15:04:53', '/information_2_col', '', '', b'0'),
(223, NULL, 'cpfw', '产品服务', 'information', 50, 'xghszh.do', '', '', '', '', '2016-03-31 16:00:59', '', '', '', b'0'),
(224, 223, 'xghszh', '香港恒生指数', 'information', 50, 'xghszh.do', '<h5>指数</h5><p><span id="nbsp_id"></span>蚁聚提供十多种股票指数，让交易者可以根据他们对美国、欧洲、亚洲及澳洲市场的看法进行交易。</p><h5>为何透过蚁聚买卖环球指数？</h5><ul class=" list-paddingleft-2"><li><p>所有指数产品均不会重新报价:为您提供快捷高效的交易执行，无需支付高昂的重新报价费用。</p></li><li><p>具竞争力的定价: 具竞争力的买卖差价，让您涉足环球市场。</p></li><li><p>免佣金: 有别于其他市场，您可免佣买卖所有蚁聚指数产品。</p></li><li><p>对冲功能: 您可买入或卖出单一指数交易。</p></li><li><p>不设止损及限价交易限制: 就多个股票指数建立止损、限价及挂单不设最小距离限制。</p></li></ul><p>&nbsp;					\n						\n</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;\n							\n &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;\n							\n &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;\n							\n &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;\n							\n &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;\n							\n &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;\n							\n &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;\n							\n &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;\n							\n &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;\n							\n &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;\n							\n &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;							</p><table class="hangqing_table" cellpadding="0" cellspacing="0" width="730"><tbody><tr class="firstRow"><th>指数名称</th>\n							<th>中文名称</th>\n							<th>合约大小</th>\n							<th>最小交易量</th>\n							<th>点值</th>\n						</tr>\n						<tr><td style="border-width: 1px; border-style: solid;">UK 100</td>\n							<td style="border-width: 1px; border-style: solid;">英国富时100指数</td>\n							<td style="border-width: 1px; border-style: solid;">1</td>\n							<td style="border-width: 1px; border-style: solid;">0.1</td>\n							<td style="border-width: 1px; border-style: solid;">0.1</td>\n						</tr>\n						<tr><td style="border-width: 1px; border-style: solid;">Wall Street 30</td>\n							<td style="border-width: 1px; border-style: solid;">道琼斯工业平均指数</td>\n							<td style="border-width: 1px; border-style: solid;">1</td>\n							<td style="border-width: 1px; border-style: solid;">1</td>\n							<td style="border-width: 1px; border-style: solid;">1</td>\n						</tr>\n						<tr><td style="border-width: 1px; border-style: solid;">US SPX 500</td>\n							<td style="border-width: 1px; border-style: solid;">美国标准普尔500指数</td>\n							<td style="border-width: 1px; border-style: solid;">25</td>\n							<td style="border-width: 1px; border-style: solid;">0.1</td>\n							<td style="border-width: 1px; border-style: solid;">2.5</td>\n						</tr>\n						<tr><td style="border-width: 1px; border-style: solid;">US Tech 100</td>\n							<td style="border-width: 1px; border-style: solid;">美国纳斯达克100指数</td>\n							<td style="border-width: 1px; border-style: solid;">10</td>\n							<td style="border-width: 1px; border-style: solid;">0.1</td>\n							<td style="border-width: 1px; border-style: solid;">1</td>\n						</tr>\n						<tr><td style="border-width: 1px; border-style: solid;">Germany 30</td>\n							<td style="border-width: 1px; border-style: solid;">德国DAX30指数</td>\n							<td style="border-width: 1px; border-style: solid;">2.5</td>\n							<td style="border-width: 1px; border-style: solid;">0.1</td>\n							<td style="border-width: 1px; border-style: solid;">0.25</td>\n						</tr>\n						<tr><td style="border-width: 1px; border-style: solid;">France 40</td>\n							<td style="border-width: 1px; border-style: solid;">法国CAC40指数</td>\n							<td style="border-width: 1px; border-style: solid;">1</td>\n							<td style="border-width: 1px; border-style: solid;">0.1</td>\n							<td style="border-width: 1px; border-style: solid;">0.1</td>\n						</tr>\n						<tr><td style="border-width: 1px; border-style: solid;">Europe 50</td>\n							<td style="border-width: 1px; border-style: solid;">欧洲Euro Stoxx 50指数</td>\n							<td style="border-width: 1px; border-style: solid;">1</td>\n							<td style="border-width: 1px; border-style: solid;">0.1</td>\n							<td style="border-width: 1px; border-style: solid;">0.1</td>\n						</tr>\n						<tr><td style="border-width: 1px; border-style: solid;">Japan 225</td>\n							<td style="border-width: 1px; border-style: solid;">日经225指数</td>\n							<td style="border-width: 1px; border-style: solid;">100</td>\n							<td style="border-width: 1px; border-style: solid;">1</td>\n							<td style="border-width: 1px; border-style: solid;">100</td>\n						</tr>\n						<tr><td style="border-width: 1px; border-style: solid;">Australia 200</td>\n							<td style="border-width: 1px; border-style: solid;">澳大利亚标准普尔200指数</td>\n							<td style="border-width: 1px; border-style: solid;">1</td>\n							<td style="border-width: 1px; border-style: solid;">0.1</td>\n							<td style="border-width: 1px; border-style: solid;">0.1</td>\n						</tr>\n						<tr><td style="border-width: 1px; border-style: solid;">Hong Kong 50</td>\n							<td style="border-width: 1px; border-style: solid;">香港恒生指数</td>\n							<td style="border-width: 1px; border-style: solid;">1</td>\n							<td style="border-width: 1px; border-style: solid;">0.5</td>\n							<td style="border-width: 1px; border-style: solid;">0.5</td></tr></tbody></table><p><br/></p>', '', '', '', '2016-04-01 17:48:31', '/information_2_col', '', '', b'0'),
(225, 223, 'hjby', '黄金白银', 'information', 50, 'hjby.do', '', '', '', '', '2016-03-31 11:20:07', '/information_2_col', '', '', b'0'),
(227, 223, 'gjyy', '国际原油', 'information', 50, 'gjyy.do', '<h4>能源</h4><p><span style="font-size: 14px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 能源产品，主要指原油炼制生产的汽油、煤油、柴油、重油以及天然气，是当前主要能源的主要供应者。原油也是全球交易最为活跃的商品之一。 蚁聚提供三种不同的能源CFD产品，分别为：美国WTI原油，英国Brent原油和天然气。</span></p><p><span style="font-size: 14px;">&nbsp;\n &nbsp;&nbsp; &nbsp; 需求因素 - \n全球能源储量，全球能源年开采量，库存，以及体现在电力，工业，居民以及商业运输使用的全球能源需求，两者之间的变化关系将显著的影响能源CFD的价格。\n供大于求，价格下跌，供不应求，则价格上涨。供应因素 - \n除石油等能源外，还有煤等其它可替代能源，它们对石油需求有着负的影响，尤其是当石油价格过高时，部分对石油的需求会转而求诸于替代能源，反之亦然 。</span></p>', '', '', '', '2016-04-01 12:16:10', '/information_2_col', '', '', b'0'),
(228, 223, 'whhb', '外汇货币', 'information', 50, 'whhb.do', '', '', '', '', '2016-04-01 12:17:24', '/information_2_col', '', '', b'0'),
(229, NULL, 'khzx', '客户中心', 'information', 50, 'zcmnzh.do', '', '', '', '', '2016-03-31 15:59:21', '', '', '', b'0'),
(230, 229, 'zcmnzh', '注册模拟账户', 'information', 50, 'zcmnzh.do', '<p class="content_p"><span class="nbsp"></span>①左侧表单请用英文、拼音及数字完整填写，每一项均为必填项。</p><p class="content_p"><span class="nbsp"></span>②成功提交后，系统生成的帐号和密码会发到您的注册邮箱，请稍后查收。 如果您的电子邮箱有垃圾邮件过滤器，请将it@fxants.com 添加到您的邮件地址簿。</p>', '', '', '', '2016-04-01 15:42:28', '/simulation', '', '', b'0'),
(231, 229, 'kszszh', '开设真实账户', 'information', 50, 'kszszh.do', '', '', '', '', '2016-03-31 16:43:46', '/real_account', '', '', b'0'),
(232, 229, 'crzj', '存入资金', 'information', 50, 'crzj.do', '<p>入金说明:</p><p class="content_p"><span class="nbsp"></span><span class="nbsp"></span><span class="nbsp"></span>1.在线入金结束后，请等待充值成成功提示再关闭浏览器，以保障您的金安全</p><p class="content_p"><span class="nbsp"></span><span class="nbsp"></span><span class="nbsp"></span>2.入金后半小时未到账，请联系客服.</p>', '', '', '', '2016-04-01 15:52:27', '/found_money', '', '', b'0'),
(233, 229, 'tkjzz', '提款及转账', 'information', 50, '', '', '', '', '', '2016-03-31 11:25:23', '/information_2_col', '', '', b'0'),
(234, NULL, 'xzzx', '下载中心', 'information', 50, 'MT4dnb.do', '', '', '', '', '2016-03-31 15:59:48', '', '', '', b'0'),
(235, 234, 'MT4dnb', 'MT4电脑版', 'information', 50, '', '', '', '', '', '2016-03-31 11:26:57', '/information_2_col', '', '', b'0'),
(236, 234, 'MT4sjb', 'MT4手机版', 'information', 50, '', '', '', '', '', '2016-03-31 11:27:37', '/information_2_col', '', '', b'0'),
(237, 234, 'wjxz', '文件下载', 'information', 50, '', '', '', '', '', '2016-03-31 11:27:58', '/information_2_col', '', '', b'0'),
(238, NULL, 'gywm', '关于我们', 'information', 50, 'gsjj.do', '', '', '', '', '2016-03-31 16:00:24', '', '', '', b'0'),
(239, 238, 'gsjj', '公司简介', 'information', 50, '', '', '', '', '', '2016-03-31 11:29:25', '/information_2_col', '', '', b'0'),
(240, 238, 'hzhb', '合作伙伴', 'information', 50, '', '', '', '', '', '2016-03-31 11:29:42', '/information_2_col', '', '', b'0'),
(242, 238, 'xwfb', '新闻发布', 'news', 50, '', '', '', '', '', '2016-03-31 17:17:21', '/news_list', '/news_content', '/news_list', b'0'),
(252, 217, 'mnjyzh', '迷你交易账户', 'information', 50, 'mnjyzh.do', '', '', '', '', '2016-04-06 17:58:53', '/information_2_col', '', '', b'0');

-- --------------------------------------------------------

--
-- 表的结构 `deposits`
--

CREATE TABLE `deposits` (
  `id` int(10) UNSIGNED NOT NULL,
  `order_no` varchar(100) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `creat_time` datetime NOT NULL,
  `payment_time` datetime DEFAULT NULL,
  `audited_time` datetime DEFAULT NULL,
  `audited_memo` varchar(200) DEFAULT NULL,
  `state` varchar(20) NOT NULL,
  `user_id` int(10) UNSIGNED NOT NULL COMMENT '用户',
  `currency` varchar(3) NOT NULL,
  `user_comment` longtext
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='入金表';

--
-- 转存表中的数据 `deposits`
--

INSERT INTO `deposits` (`id`, `order_no`, `amount`, `creat_time`, `payment_time`, `audited_time`, `audited_memo`, `state`, `user_id`, `currency`, `user_comment`) VALUES
(17, 'gct20160322124342', '100.00', '2016-03-22 12:43:42', '2016-03-22 12:47:12', NULL, '', 'DEPOSITED', 1, 'USD', NULL),
(18, 'gct20160330095032', '100000.00', '2016-03-30 09:50:32', '2016-03-30 09:50:44', NULL, '', 'DEPOSITED', 37, 'USD', NULL);

-- --------------------------------------------------------

--
-- 表的结构 `email_validation`
--

CREATE TABLE `email_validation` (
  `id` int(11) NOT NULL,
  `email_num` varchar(36) DEFAULT NULL,
  `email_time` datetime DEFAULT NULL,
  `pass_email_time` datetime DEFAULT NULL,
  `state` varchar(255) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `groups`
--

CREATE TABLE `groups` (
  `id` int(10) UNSIGNED NOT NULL,
  `name` varchar(50) NOT NULL COMMENT '组名',
  `rate` double NOT NULL COMMENT '费率',
  `sort_num` int(11) NOT NULL DEFAULT '50' COMMENT '排序数'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户组表';

--
-- 转存表中的数据 `groups`
--

INSERT INTO `groups` (`id`, `name`, `rate`, `sort_num`) VALUES
(1, '1组', 0.01, 1),
(2, '2组', 0.02, 2);

-- --------------------------------------------------------

--
-- 表的结构 `images`
--

CREATE TABLE `images` (
  `id` int(10) UNSIGNED NOT NULL,
  `archive_id` int(10) UNSIGNED NOT NULL,
  `type` varchar(50) NOT NULL,
  `name` varchar(100) NOT NULL,
  `path` varchar(200) NOT NULL,
  `creat_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `links`
--

CREATE TABLE `links` (
  `id` int(10) UNSIGNED NOT NULL,
  `column_id` int(10) UNSIGNED NOT NULL,
  `title` varchar(100) NOT NULL,
  `url` varchar(200) NOT NULL,
  `creat_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sort_num` int(11) NOT NULL DEFAULT '50',
  `is_bold` tinyint(1) NOT NULL DEFAULT '0',
  `is_top` tinyint(1) NOT NULL DEFAULT '0',
  `is_show` bit(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `news`
--

CREATE TABLE `news` (
  `body` longtext NOT NULL,
  `creat_time` datetime NOT NULL,
  `sort_num` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `is_bold` bit(1) NOT NULL,
  `is_show` bit(1) NOT NULL,
  `is_top` bit(1) NOT NULL,
  `id` int(10) UNSIGNED NOT NULL,
  `column_id` int(10) UNSIGNED NOT NULL,
  `seo_description` varchar(200) NOT NULL,
  `seo_keywords` varchar(200) NOT NULL,
  `seo_title` varchar(80) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `news`
--

INSERT INTO `news` (`body`, `creat_time`, `sort_num`, `title`, `is_bold`, `is_show`, `is_top`, `id`, `column_id`, `seo_description`, `seo_keywords`, `seo_title`) VALUES
('<p>原标题：习近平会见韩国总统朴槿惠 　　</p><p>　　新华社华盛顿3月31日电（记者 刘华 魏建华）31日，国家主席习近平在华盛顿会见韩国总统朴槿惠，就双边关系及共同关心的重大国际和地区问题深入交换意见。</p><p>　　习近平指出，中韩建交24年来，两国关系始终保持平稳快速发展。中方高度重视中韩关系，始终把发展对韩关系放在周边外交的重要位置。当前，我和\n朴槿惠总统达成的一系列重要共识正在得到全面落实，取得许多积极成果。把中韩关系维护好、巩固好、发展好，是我们共同肩负的历史使命。中方愿同韩方深化各\n领域交流合作，确保中韩关系始终在正确轨道上健康稳定发展。</p><p>　　习近平强调，中韩双方要保持高层互动势头，用好一系列战略沟通机制密切协调，照顾彼此重大关切，尊重对方主权、安全、发展利益。中方欢迎韩方积\n极参与“一带一路”建设。双方要加快推进国家发展战略对接，重点做好自由贸易协定实施工作，深化金融合作，推动中韩产业园建设尽快取得实质进展，推进东亚\n经济一体化进程。要扩大人文交流，相互支持办好2018年平昌冬季奥运会和2022年北京冬季奥运会。中方愿同韩方一道，办好韩国旅游年、青年领导者论坛\n等项目，鼓励两国人民交流，服务“一千万+”时代人员往来。双方要密切在联合国、二十国集团、亚太经合组织等机制内合作，加强在全球性问题上的沟通协调。</p><p><ins style="display: block; overflow: hidden; text-decoration: none;" data-ad-offset-top="0" data-ad-offset-left="0" class="sinaads sinaads-done" id="Sinads49447" data-ad-pdps="PDPS000000044086" data-ad-status="done"><ins style="text-decoration:none;margin:0px auto;width:300px;display:block;position:relative;"></ins></ins></p><p>　\n　朴槿惠表示，赞同习近平主席对韩中关系的评价。韩中保持密切的高层交往是两国关系重要性的体现。韩方高度重视对华关系，愿在深化互信的基础上推动韩中战\n略合作伙伴关系不断向前发展，赞同双方加强高层战略沟通，并在各层级、各领域密切交流。在当前全球经济形势下，韩中双方更有必要加强经贸等领域务实合作，\n加快实施韩中自贸协定，实现“欧亚合作倡议”同“一带一路”建设对接。韩方支持密切韩中人文交流，相互支持办好平昌冬奥会和北京冬奥会。</p><p>　　习近平欢迎朴槿惠9月赴华出席二十国集团领导人杭州峰会。朴槿惠接受了邀请，表示韩方愿为峰会成功作出积极贡献。</p><p>　　两国元首还就朝鲜半岛形势交换了看法。习近平强调，中方坚持实现半岛无核化、维护半岛和平稳定、通过对话协商解决问题，主张各方都应全面完整履\n行安理会相关决议。中方敦促各方避免采取任何可能加剧局势紧张的言行，不得损害本地区国家的安全利益和战略平衡。对话协商是解决问题的唯一正确方向，中方\n愿作出建设性努力，推动在六方会谈框架下重启对话。朴槿惠表明了韩方对当前半岛形势的看法，表示愿就有关问题同中方保持密切沟通。</p><p>　　王沪宁、栗战书、杨洁篪等参加会见。（完）</p><p><br/></p>', '2016-04-01 11:50:17', 50, '习近平会见朴槿惠 就朝鲜半岛形势交换看法', b'0', b'1', b'0', 245, 242, '', '', ''),
('<p><strong>凤凰财经讯</strong> 中国企业2016年来海外并购的速度令人大呼看不懂。</p><p>彭博数据显示，中国企业年内迄今已宣布的海外并购交易规模达1130亿<a href="http://app.finance.ifeng.com/hq/rmb/quote.php?symbol=USD" target="_blank"><span style="color:#004276">美元</span></a>，不仅超过2014年全年而且接近去年创纪录的1210亿美元水平。其中对美国企业的并购达到410亿美元，已经达到去年全年的两倍水平。</p><p class="detailPic"><img src="http://y0.ifengimg.com/a/2016_14/bedeea7d972d189.bmp" alt=""/></p><p>高盛全球并购业务联合负责人Gregg Lemkau表示，今年中国企业海外并购活动有可能达到去年的五倍，而很多大型交易中涉及的中国公司，外界甚至闻所未闻。Lemkau称，<span style="font-family: 宋体, 黑体, 楷体, 仿宋, Arial, Verdana, sans-serif;">人民币汇率是因素之一，</span><span style="text-indent: 2em; font-family: 宋体, 黑体, 楷体, 仿宋, Arial, Verdana, sans-serif;">“</span><span style="text-indent: 2em; font-family: 宋体, 黑体, 楷体, 仿宋, Arial, Verdana, sans-serif;">在贬值之前以海外价值更高的货币投资于美元或者欧元资’‘似乎是当务之急’。”</span></p><p>中金公司分析师王汉锋在研报也表示，<a href="http://app.finance.ifeng.com/hq/rmb/list.php" target="_blank"><span style="color:#004276">人民币</span></a>未来双向波动的可能性更大，而非过去多年的单向升值，促使企业考虑将剩余资金配置在海外资产。</p><p>知名<a href="http://auto.ifeng.com/news/finance/" target="_blank"><span style="color:#004276">金融</span></a>博客Zerohedge评论，并购是国内寡头绕过<a href="http://finance.ifeng.com/app/hq/hkstock/hk00170/" target="_blank" title="中国资本 00170"><span style="color:#004276">中国资本</span></a><span id="hk00170_hq">[0.00%]</span>管制、将数十亿股票放在美国或国际土壤上的最后机会。这也解释了国内企业在急于拿下收购目标时对价格完全不敏感的原因，不同于传统并购时竞购方希望把购买价格压低，在中企的海外并购中，用于海外投资的金额越多，能够规避中国金融体系的资金也就越多。</p><p>中国企业为何在海外敢于出手买买买？背后深度原因，看看量化投资专家贝乐斯的分析。</p><p>在今年的博鳌论坛上，哈继铭博士说了这么一段话：“中国的货币增长速度太快，中国货币1月份（M2）增长14%，你那么快的增长速度如果说<a href="http://app.finance.ifeng.com/hq/rmb/list.php" target="_blank"><span style="color:#004276">汇率</span></a>还不贬值的话，按这个逻辑推下去要不了几年，中国可以把全世界的资产都买下来了，这是荒唐的事情，这不可能的嘛，所以你要么货币增速下降，但是货币增速下降可能对稳增长不利，要么你就得贬值，两者之间必须要取一项的。”</p><p class="detailPic"><img src="/backoffice/upload/image/20160401/1459482846620077823.jpg" alt="" height="291" width="500"/></p><p>一图胜千言。哈博的话用图表来说明就非常清楚了。自2007年1月以来，中国的<a href="http://app.finance.ifeng.com/data/mac/month_idx.php?type=015" target="_blank"><span style="color:#004276">货币供应</span></a>总量M2连续超越了日本、欧洲和美国几大经济体，目前相当于欧洲和美国的两倍，日本的三倍。中国的M2已经相当于这三大经济体的M2总量的70%左右。这么高的M2，需要两个重要的驱动因素：央行的资产负债表扩张+ 银行系统的信用扩张。</p><p class="detailPic"><img src="/backoffice/upload/image/20160401/1459482846730064315.jpg" alt="" height="291" width="500"/></p><p>上面这张图可以看出中国央行有多努力。中国央行的资产负债表雄冠全球，超越了美联储和欧洲央行及日本央行。但是，如果仔细看央行资产负债表的变化曲线，中国央行的曲线与美联储的曲线非常神似。如果做一个相关系数分析，可以<a href="http://car.auto.ifeng.com/series/2087/" target="_blank"><span style="color:#004276">发现</span></a>相关系数高达95.96%，也就是说美联储的资产负债表变化可以解释高达95.96%的中国央行资产负债表变化。美联储其实就是中国央行的央行。这背后的原因很可能是因为中国的基础货币发行一直依赖外汇占款，而<a href="http://app.finance.ifeng.com/hq/rmb/list.php" target="_blank"><span style="color:#004276">人民币</span></a>又长期盯住美元，所以让美联储变成了中国央行的央行。</p><p class="detailPic"><img src="/backoffice/upload/image/20160401/1459482847047005229.jpg" alt="" height="208" width="500"/></p><p>M2\n增长只有央行增加基础货币，扩大资产负债表还不行，必须有银行系统的配合，信用扩张才行。从美联储到中国的M2货币供应总量，是一个层级放大系统。美联储\nQE，扩大资产负债表，增加基础货币，美国及海外的银行系统信用扩张，传递到中国，变为外汇占款，增加中国的基础货币，投入中国的银行系统，中国的银行系\n统信用扩张，经过贷款变存款，存款变贷款的正反馈过程，最终推升了中国的M2货币供应。</p><p>现在，美联储的QE已经停止，资产负债表也已经停\n止增长。由于资本流出中国，之前的层级放大系统开始反转。外汇占款下降，基础货币下降，中国央行资产负债表收缩。但是，为了维持经济体系中的僵尸企业借新\n还旧，M2还必须指数增长。这就产生了巨大的矛盾。因此，中国央行必须在外汇占款之外另辟蹊径，增加基础货币，而且，银行系统必须加大信用扩张，发放更多\n的贷款。</p><p class="detailPic"><img src="/backoffice/upload/image/20160401/1459482847086031281.jpg" alt="" height="347" width="500"/></p><p>在\n这个过程中，美联储的作用就非常大了。如果美联储持续快速升息，只会加速推动资本流出中国，造成经济硬着陆，人民币贬值，从而震动全球市场，反过来影响美\n国经济。作为中国央行的央行，美联储必须考虑自己加息的后果，不得不谨慎。另一方面，即使美联储不升息，但中国央行大量印钞增加基础货币，同时银行系统加\n大信用扩张，M2还会按照既定的13%左右的速度复利增长。这样一来，M2的增速保住了，僵尸企业的借新还旧也解决了，皆大欢喜。唯一的问题是，这么多\n钱，汇率也不变，中国公司完全可以出去买买买，买下全世界。因此，今年第一季度中国企业海外并购总额超过去年全年就非常合理了。</p><p class="detailPic"><img src="/backoffice/upload/image/20160401/1459482847140044698.jpg" alt="" height="288" width="500"/></p><p>这\n样大规模的海外并购，如此快速的增长，对外汇占款的消耗非常大，远远超过大妈们一人五万美元的换汇，而且绝不是“藏汇于民”，而是真金白银的外汇流出。因\n此，央行必须在维持汇率稳定与维持全世界最高的M2以13%的复利增长之间做出选择。有的时候，维持现状，也是一种选择，那就意味着让自然的规律测试人类\n认知的边界。</p><p><strong>附：看看中国企业近期都买了什么？</strong></p><p>能源化工</p><p>中国化工2月宣布以每股465美元的现金收购先正达，收购金额超430亿美元，成中国企业最大的海外并购交易。</p><p>豪华酒店</p><p>Strategic Hotels &amp; Resorts在美国拥有16家豪华酒店，包括位于圣地亚哥海滨的科罗拉多<a href="http://finance.ifeng.com/app/hq/hkstock/hk00045/" target="_blank" title="大酒店 00045"><span style="color:#004276">大酒店</span></a><span id="hk00045_hq">[<span class="Agreen">-0.49%</span>]</span>以及坐落于纽约中央公园南侧的艾塞克斯豪斯JW万豪酒店。安邦保险同意斥资65亿美元收购Strategic Hotels &amp; Resorts，仅3个月前，美国私人股本集团黑石(Blackstone)才将这些豪华酒店收归私有。</p><p>超级豪华酒店</p><p>喜达屋旗下拥有喜来登和威斯汀等品牌。喜达屋拥有的房地产价值约为40亿美元。万豪去年11月欲以122亿美元收购喜达屋，但后来安邦加入竞购。如今，中国保险商安邦集团将其对美国酒店运营商喜达屋集团的收购报价提高至140亿美元。</p><p>家用电器</p><p>通用电气(GE.N)同意以54亿美元将家用电器业务出售给海尔公司。之前瑞典家电制造商伊莱克斯提出以33亿美元收购通用家电业务，美国司法部以反垄断为由阻止了该交易。</p><p>起重机</p><p><a href="http://finance.ifeng.com/app/hq/stock/sz000157/" target="_blank" title="中联重科 000157"><span style="color:#004276">中联重科</span></a><span id="sz000157_hq">[<span class="Agreen">-1.31%</span> <a href="http://app.finance.ifeng.com/data/stock/tab_zjlx.php?code=sz000157" target="_blank" style="font-weight: normal">资金</a> <a href="http://star.finance.ifeng.com/stock/sz000157" target="_blank" style="font-weight: normal">研报</a>]</span>欲收购美国起重机生产商特雷克斯公司（Terex Corp），挑战特雷克斯与芬兰起重机制造商科尼集团（Konecranes）的合并意向，并且提高出价至每股31美元。据此推算，收购总价已由此前的约33亿美元上涨至约34亿美元。</p><p>《蝙蝠侠》的好莱坞制片商</p><p>今年一月万达集团宣布以不超过35亿美元现金（约230亿元人民币）收购美国传奇影业公司。传奇影业已出品了包括《蝙蝠侠》系列、《盗梦空间》、《侏罗纪世界》在内的多部大片。</p><p>软件分销商</p><p>2月海航集团宣布以60亿美元(约合391.17亿元人民币)价值将全球电子分销商巨头英迈(Ingram Micro)收编旗下。加入海航的英迈将成为海航集团旗下收入最高的子公司，并夯实海航的物流业务以及加快其全球化进程。</p><p>同性恋约会App</p><p>中国大型网络游戏企业昆仑万维拟以约9300万美元收购美国New Grindr LLC的60%股权，后者是全球最大的同性恋社交网络。</p><p>股票交易所</p><p>芝加哥交易所(CME.O)已经同意接受中国重庆财信企业集团领导的投资者联盟收购。有134年历史的芝加哥证交所处理约0.5%的美股交易，这笔收购案一旦完成，芝加哥证交所也将成为第一家被中国企业收购的美国证券交易所。</p><p>然而，中国企业海外收购也面临着不小的阻碍。比如，中国重庆财信企业集团欲收购芝加哥股票交易所，导致45名美国议员联名致信美国财政部，要求对此项收购进行“全面和严格的调查”。</p><p>中\n国对美国企业的收购一直在增加，引发了美国政府更多的国家安全审查。2013年中国对美投资只排在第14位，远不及传统的领跑者英国、日本和加拿大。然而\n近年来，在负责审查外资收购美国企业的交易是否会对国家安全产生影响的外国在美投资委员会(CFIUS)的审查清单上，中国的并购交易却位居榜首。</p><p><br/></p>', '2016-04-01 11:53:11', 50, '豪掷千亿美金！中国企业为何要在海外买买买？', b'0', b'1', b'0', 246, 242, '', '', ''),
('<p><strong>凤凰财经讯</strong>&nbsp;博鳌亚洲论坛2016年年会于3月22-25日在海南博鳌召开，主题为“亚洲新未来：新活力与新愿景”，凤凰财经全程报道。</p><p>在““闯祸”的杠杆”分论坛上，高盛投资管理部中国副主席暨首席投资策略师哈继铭在谈到<a href="http://app.finance.ifeng.com/hq/rmb/list.php" target="_blank"><span style="color:#004276">人民币</span></a><a href="http://app.finance.ifeng.com/hq/rmb/list.php" target="_blank"><span style="color:#004276">汇率</span></a>时表示，一个国家如果汇率对内不断贬值的话，是没有对外升值的基础。你可以想一下，过去100元<a href="http://app.finance.ifeng.com/hq/rmb/list.php" target="_blank"><span style="color:#004276">人民币</span></a>到超市能买多少东西，今天能买多少东西。你过去100、200万能买什么房子，现在能买什么房子。</p><p>他更是表示，如果一个货币每年以14%、15%速度增长，还不贬值的话，按照这个逻辑推下去，是不是几年之后中国可以把全世界资产都买下来，不可能的。要么M2降下去，要么有贬值趋势。</p><p><strong>以下为发言实录：</strong></p><p>哈继铭：我坚信中国在短期内是有能力保持汇率的稳定，因为中国有依然比较庞大的<a href="http://app.finance.ifeng.com/data/mac/month_idx.php?type=016&symbol=01602" target="_blank"><span style="color:#004276">外汇储备</span></a>。第二，中国也在加强资本管制。所以说中国会不会马上来一个一次性大幅贬值，我觉得这个可能性不大，只要政府不想贬，至少一段时期内可以做到。</p><p>但\n是作为经济学家，我难以判断长期的经济走势，但是我可以从基本面上谈一些观点供大家参考。我觉得一个国家的汇率，短期是政策决定的。中期是外贸的状况决定\n的。更长期来看是这个国家货币购买力来决定的。从中期来看，目前中国的出口是在大幅下降，1月份下降，2月份下降25%。随着各类生产成本，劳动力生产成\n本、土地成本上升，中国的优势不断被削弱。</p><p>可能将来的出口未必有一个反弹的机会。也就是说中国的外贸顺差也许在某一个时刻开始收紧。其实现在外贸顺差占<a href="http://app.finance.ifeng.com/data/mac/year_idx.php?type=001&symbol=00102" target="_blank"><span style="color:#004276">GDP</span></a>比重和前几年比已经不可同日而语了。前几年10%多，现在2%左右。随着外贸顺差缩小，人民币升值可能性越来越小，反而贬值压力存在，尤其是资金开始往外面流出。他会抵消外贸顺差，从而使外汇储备不断下降，从而产生一个贬值压力。</p><p>更中长期一些看，一个国家如果汇率对内不断贬值的话，是没有对外升值的基础。你可以想一下，过去100元人民币到超市能买多少东西，今天能买多少东西。你过去100、200万能买什么房子，现在能买什么房子。</p><p>所以其实你看中国现在东西，你和美国、和日本、和香港相比，有很多东西越来越多的东西是越来越贵，说明人民币购买能力是在不断下降。长此以往，这个汇率也可能有贬值压力。</p><p>我再说一句，如果你一个货币每年以14%、15%速度增长，还不贬值的话，按照这个逻辑推下去，是不是几年之后中国可以把全世界资产都买下来，不可能的。要么降下去，要么有贬值趋势。</p><p><br/></p>', '2016-04-01 11:55:18', 50, '哈继铭：M2每年14%的增长 人民币不可能不贬值', b'0', b'1', b'0', 247, 242, '', '', ''),
('<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 今日央行发布长达73页的<span style="text-indent: 2em;">2015年第四季度中国货币政策执行报告。</span></p><p>央\n行在报告中提出了下一阶段中国的货币政策思路。下一阶段，央行保持政策的连续性和稳定性，继续实施稳健的货币政策，保持松紧适度，适时预调微调，做好与供\n给侧结构性改革相适应的总需求管理。 综合运用数量、价格等多种货币政策工具，优化政策组合，加强和改 \n善宏观审慎管理，疏通货币政策传导渠道，从量价两方面为结构调整 和转型升级营造适宜的货币<a href="http://auto.ifeng.com/news/finance/" target="_blank"><span style="color:#004276">金融</span></a>环境。</p><p>报告还谈到了近期人们比较关注的货币政策对<a href="http://app.finance.ifeng.com/hq/rmb/list.php" target="_blank"><span style="color:#004276">汇率</span></a>、<a href="http://app.finance.ifeng.com/data/mac/month_idx.php?type=016&symbol=01602" target="_blank"><span style="color:#004276">外汇储备</span></a>的影响。报告称，在开放宏观格局下，货币政策面临着汇率“硬约束”以及资产价格国际间“强对比”地约束。<strong>本国货币政策过于宽松容易导致本币贬值压力。如果国内<a href="http://app.finance.ifeng.com/data/mac/jmxf.php" target="_blank"><span style="color:#004276">物价</span></a>和房价过高，投资就可能转到国外资产价格更低的地方。这些都会表现为资本外流以及外汇储备的下降。</strong></p><p><br/></p>', '2016-04-01 11:56:13', 50, '央行：降准会导致人民币贬值、资本外流和外储下降', b'0', b'1', b'0', 248, 242, '', '', ''),
('<p class="detailPic"><img src="/backoffice/upload/image/20160401/1459483050955081174.png" alt="" height="414" width="620"/></p><p class="picIntro">新闻配图</p><p>3月份的投资市场虽算不上一帆风顺，但和前两个月比较却是大有起色，作为投资者，或许关注踏入第二季市况能否再试高位，抑或波动将会重现。</p><p>市场会有升跌，对3月份反弹并不意外，特别是股市自相当超卖的水平回升显得相当合理，但同时有反弹未必能持续太长时间。</p><p>幸好近期我们见到全球央行对利率政策看法，特别是近期的言论均较早前变得更为“鸽派”，另一方面企业盈利的预期亦开始出现回稳迹象，更重要的是股市\n动力亦确实在过去一个月见到改善。虽然如此，美国标普500指数现时与关键阻力位只有约5%的距离，基于美国股市占全球股市指标逾五成，意味除非基本因素\n进一步改善，否则股市上升空间将受到限制。</p><p>投资者可考虑利用是次风险资产的反弹，将组合中部分股票转为债券或另类投资策略。债券方面，偏好企业发行的债券，特别是美国投资级别以及高收益债券，而个别国家债券亦有望偏强，但表现料不及企业债。</p><p>中国近期的政策偏向支持短线的经济增长，故预期内地的数据或会在未来2至3个月好转，另一方面，中国央行已经透过利用<a href="http://app.finance.ifeng.com/hq/rmb/list.php" target="_blank"><span style="color:#004276">人民币</span></a>以增添政策的灵活性。早前在<a href="http://app.finance.ifeng.com/hq/rmb/quote.php?symbol=USD" target="_blank"><span style="color:#004276">美元</span></a>转弱下，央行下调<a href="http://app.finance.ifeng.com/hq/rmb/list.php" target="_blank"><span style="color:#004276">人民币</span></a>兑一篮子贸易货币的汇价，但另一方面则上调兑美元的汇价；然而，若美元在接下来数周回升，将令有关情况变得更具挑战性，而中国如何回应亦将影响上月资本外流减慢的情况能否延续下去。如果资本外流速度有所回升，料将为股市以致是其他风险资产带来负面影响。</p><p><strong style="margin: 0px; padding: 0px; max-width: 100%; box-sizing: border-box !important; word-wrap: break-word !important;">美元下半年会“王者归来”&nbsp;</strong></p><p>澳新银行(ANZ)外汇策略师Daniel Breen和Brian Martin认为，近期美元疲软态势可能会持续一段时间，但判断美元升势已经完结为时尚早。</p><p>从中期(12个月)来看，相信宣布美元反弹结束还太早。我们仍然认为，过了下一季度之后美元兑亚洲货币和大宗商品货币的最终路径还会是向上，这是由于许多结构性趋势依然完好，最后，这些因素将盖过当前正在经历的周期性改善所产生的影响。</p><p>总体而言，在决定<a href="http://app.finance.ifeng.com/hq/rmb/list.php" target="_blank"><span style="color:#004276">汇率</span></a>走向时央行将起到较小的作用；不过美联储的决定对于市场表现依然重要。</p><p>我们的分析显示，就业一直是影响核心<a href="http://finance.ifeng.com/topic/news/tongzhang/index.shtml" target="_blank"><span style="color:#004276">通胀</span></a>的最重要因素。在利率没有大幅升至美联储预期水平的情况下，弱势美元对提振<a href="http://finance.ifeng.com/topic/news/tongzhang/index.shtml" target="_blank"><span style="color:#004276">通胀</span></a>没有很大空间。这应会限制美联储发表鸽派言论的持续时间。</p><p>下图显示，即便弱势美元对核心<a href="http://finance.ifeng.com/topic/news/tongzhang/index.shtml" target="_blank"><span style="color:#004276">通胀</span></a>构成拖累，但在就业市场状况收紧的背景下<a href="http://app.finance.ifeng.com/data/mac/jmxf.php" target="_blank"><span style="color:#004276">物价</span></a>压力仍继续攀升。</p><p class="detailPic"><img src="/backoffice/upload/image/20160401/1459483051314012500.png" alt="" height="441" width="575"/></p><p class="picIntro">数据来源：彭博,ANZ</p><p>假如所有这些趋势持续，在进入今年下半年之后，美联储会<a href="http://car.auto.ifeng.com/series/2087/" target="_blank"><span style="color:#004276">发现</span></a>自己太保守了，而长期名义债券收益率将攀升，以对经济增长改善做出回应。</p><p>澳新银行提供了该机构对于美元兑主要货币的预估。除了兑<a href="http://app.finance.ifeng.com/hq/rmb/quote.php?symbol=GBP" target="_blank"><span style="color:#004276">英镑</span></a>之外，澳新预计美元在今年下半年将重拾升势。</p><p><strong style="margin: 0px; padding: 0px; max-width: 100%; box-sizing: border-box !important; word-wrap: break-word !important;">油价不会重回2月低位</strong></p><p>近期<a href="http://app.finance.ifeng.com/data/indu/cpjg.php?symbol=285" target="_blank"><span style="color:#004276">国际油价</span></a>急升，不过短线<a href="http://app.finance.ifeng.com/data/indu/cpjg.php?symbol=285" target="_blank"><span style="color:#004276">原油</span></a>供过于求的情况未有改变。除非美国原油生产量下降，又或是全球原油需求上升，油价才有望显著走高，这情况或要数个月时间才有望达成。除油价之外其他商品难见起色，特别是工业用金属。</p><p>巴克莱银行(Barclays)在报告中称，油价不太可能再重回今年2月的低位，预计今年上半年油价将在每桶35-45美元间交投，第四季度回升至每桶45美元。</p><p>眼下宏观经济忧虑和高库存是原油市场的巨大‘枷锁’，预计2016年一、二季度油价会在每桶35-45美元区间内交投。不过，油价年内不太可能再次跌至2月低位。</p><p>预计，美国原油(WTI)均价将在今年四季度达到每桶45美元，而这将刺激一些美国页岩油生产商再度启动钻探。</p><p>尽管我们预计今年下半年油价将逐步上扬，但这可能会优先体现在库存方面。一旦库存下滑，美国页岩油生产商可能无法很快回应市场对新产量的需求，这就会导致油价走高。原油市场供应面调整最终必会发生，页岩油产量可能需要到2017年初才会逐步走高。</p><p><strong style="margin: 0px; padding: 0px; max-width: 100%; box-sizing: border-box !important; word-wrap: break-word !important;">美股警灯闪烁“死亡十字”高悬</strong></p><p>尽管过去五周美股大幅反弹，基准标准普尔500指数的50日移动均线指数仍低于200日移动均线切入位。历史走势表明，这释放出一警告信号。</p><p>MarketWacth专栏作家Brett Arends撰文指出，以前对诸如“死亡十字”之类的指标持有怀疑态度，但几个月前改变了这一观点。其改变主意的原因很简单：那就是：数据。</p><p>从Burton Malkiel到巴菲特(Warren Buffett)等许多极其智慧的股市圣贤和大师都表示，类似的技术指标只是<a href="http://auto.ifeng.com/news/finance/" target="_blank"><span style="color:#004276">金融</span></a>“巫术”而已，应该被忽略。Arends表示，其之前认为这一观点是正确的。其对“死亡十字”等哗众取宠的标签一向保持高度警惕。</p><p class="detailPic"><img src="/backoffice/upload/image/20160401/1459483051537011743.png" alt="" height="398" width="639"/></p><p class="picIntro" style="margin-left: 40px; text-align: left;">但我认为固执是封闭的标志。从不改变主意的人永远学不到新鲜事物。因此我回顾了1920年代以来的所有“死亡十字”，并惊讶的发现其远比我之前认为的更加有用。</p><p>简而言之：跟随这一个指标的市场人士将囊括股市所有的长期增长势头，其中仅包括一小部分波动性和风险。当50日均线低于200日均线时，削减你的股市敞口；而当50日均线超过200日均线时增加持仓，这一指标可以使你在股市大崩跌之前退出，而在大牛市期间待在里面。</p><p>谁也无法保证未来走势会跟过去一样，但这就是赌注的方式。许多技术指标被作为市场方向的快速、短期信号。死亡十字更适合长期投资者。这一指标往往变化不大，可以为长期市场趋势指明方向。</p><p>1月8日的两条线交叉表明是时候抛售股票了。如今，标普500指数50日均线低于200日均线约3%。</p><p>今年春天，股市持续上涨可能会把红灯转成绿灯。但这在5月1日之前不太可能实现，股票市场知识(及数据)表明，投资者应该“5月做空并离场。也许我\n们都应该度过一个加长版暑假，等待秋天的时候再决定是否入市，而届时我们也可以更清楚的了解特朗普是否真正有可能当上美国总统。</p><p><strong style="margin: 0px; padding: 0px; max-width: 100%; box-sizing: border-box !important; word-wrap: break-word !important;">黄金是投资组合多样化工具</strong></p><p>今年以来，受到市场震荡的推动，黄金的避险需求再次凸显，金价持续走高。</p><p>贝莱德(BlackRock)称，推荐和<a href="http://finance.ifeng.com/topic/news/tongzhang/index.shtml" target="_blank"><span style="color:#004276">通胀</span></a>相关的债券和黄金，认为<a href="http://finance.ifeng.com/topic/news/tongzhang/index.shtml" target="_blank"><span style="color:#004276">通胀</span></a>的风险将增加。<a href="http://finance.ifeng.com/topic/news/tongzhang/index.shtml" target="_blank"><span style="color:#004276">通胀</span></a>相关的债券和黄金可作为多样化工具。</p><p>而此前，PIMCO已经推荐持有黄金，作为投资组合的多样化工具。</p><p>ECU Group首席技术策略师Robin Griffiths表示：在把钱存银行没有任何回报之后，黄金本身无收益就不再是不利因素了。黄金对冲的风险不仅有<a href="http://finance.ifeng.com/topic/news/tongzhang/index.shtml" target="_blank"><span style="color:#004276">通胀</span></a>，还有通缩。</p><p>对黄金而言，其本身无收益且有储存成本都是不利因素。但在目前的市场环境下，黄金的这些不利因素越来越淡化，今年至今，金价已经上涨了超过15%。</p><p>如果金价上行能够突破1350美元/盎司水平，那么将再触历史新高。</p><p><strong style="margin: 0px; padding: 0px; max-width: 100%; box-sizing: border-box !important; word-wrap: break-word !important;">总结</strong></p><p>总体而言，4月份的市况是审慎乐观，乐观源于基本因素有改善的迹象，但因过去一个月升幅甚大，风险犹存。短线风险资产的反弹或会延续多一点时间，但回吐的风险亦随之而上升，亦是投资者考虑是否需要调整组合中股票及债券比重的合适时间。</p><p><br/></p>', '2016-04-01 11:57:35', 50, ' 4月，趁反弹调整投资组合', b'0', b'1', b'0', 249, 242, '', '', ''),
('<p>这几天一个意外惊喜在A股呈现：继中央汇金和证金公司之后，外汇局旗下三大平台在去年四季度大举入市，除了已被热炒的梧桐树投资平台，还包括坤藤投资和凤山投资，持有A股市值至少达到295.05亿元。</p><p>投资者朋友不禁要问，外汇局三大平台谁来掌舵？持有的A股有哪些特点？增持A股的背景是什么？</p><p><strong>所持上市公司全景扫描</strong></p><p>据上市公司年报，梧桐树投资已经入<a href="http://finance.ifeng.com/app/hq/stock/sh600000/" target="_blank" title="浦发银行 600000"><span style="color:#004276">浦发银行</span></a><span id="sh600000_hq">[<span class="Agreen">-1.67%</span> <a href="http://app.finance.ifeng.com/data/stock/tab_zjlx.php?code=sh600000" target="_blank" style="font-weight: normal">资金</a> <a href="http://star.finance.ifeng.com/stock/sh600000" target="_blank" style="font-weight: normal">研报</a>]</span>、<a href="http://finance.ifeng.com/app/hq/stock/sh601398/" target="_blank" title="工商银行 601398"><span style="color:#004276">工商银行</span></a><span id="sh601398_hq">[<span class="Agreen">-0.47%</span> <a href="http://app.finance.ifeng.com/data/stock/tab_zjlx.php?code=sh601398" target="_blank" style="font-weight: normal">资金</a> <a href="http://star.finance.ifeng.com/stock/sh601398" target="_blank" style="font-weight: normal">研报</a>]</span>、<a href="http://finance.ifeng.com/app/hq/stock/sh601328/" target="_blank" title="交通银行 601328"><span style="color:#004276">交通银行</span></a><span id="sh601328_hq">[<span class="Agreen">-0.54%</span> <a href="http://app.finance.ifeng.com/data/stock/tab_zjlx.php?code=sh601328" target="_blank" style="font-weight: normal">资金</a> <a href="http://star.finance.ifeng.com/stock/sh601328" target="_blank" style="font-weight: normal">研报</a>]</span>、<a href="http://finance.ifeng.com/app/hq/stock/sh601988/" target="_blank" title="中国银行 601988"><span style="color:#004276">中国银行</span></a><span id="sh601988_hq">[<span class="Agreen">-1.18%</span> <a href="http://app.finance.ifeng.com/data/stock/tab_zjlx.php?code=sh601988" target="_blank" style="font-weight: normal">资金</a> <a href="http://star.finance.ifeng.com/stock/sh601988" target="_blank" style="font-weight: normal">研报</a>]</span>、<a href="http://finance.ifeng.com/app/hq/stock/sh601788/" target="_blank" title="光大证券 601788"><span style="color:#004276">光大证券</span></a><span id="sh601788_hq">[<span class="Agreen">-0.79%</span> <a href="http://app.finance.ifeng.com/data/stock/tab_zjlx.php?code=sh601788" target="_blank" style="font-weight: normal">资金</a> <a href="http://star.finance.ifeng.com/stock/sh601788" target="_blank" style="font-weight: normal">研报</a>]</span>前十大流通股东，持有市值分别达到111.11亿元、65.07亿元、51.17亿元、42.51亿元和1.9亿元。</p><p class="detailPic"><img src="/backoffice/upload/image/20160401/1459483185630091553.jpg" alt=""/></p><p class="picIntro">图</p><p>由此可见，梧桐树投资这一平台主要持有的是<a href="http://auto.ifeng.com/news/finance/" target="_blank"><span style="color:#004276">金融</span></a>行业，且除光大证券外，其余四家公司均为银行，市盈率均在10倍以内，合计持有271.75亿元。</p><p>而凤山投资已经入<a href="http://finance.ifeng.com/app/hq/stock/sh600050/" target="_blank" title="中国联通 600050"><span style="color:#004276">中国联通</span></a><span id="sh600050_hq">[<span class="Ared">1.82%</span> <a href="http://app.finance.ifeng.com/data/stock/tab_zjlx.php?code=sh600050" target="_blank" style="font-weight: normal">资金</a> <a href="http://star.finance.ifeng.com/stock/sh600050" target="_blank" style="font-weight: normal">研报</a>]</span>、<a href="http://finance.ifeng.com/app/hq/stock/sh600018/" target="_blank" title="上港集团 600018"><span style="color:#004276">上港集团</span></a><span id="sh600018_hq">[<span class="Agreen">-4.14%</span> <a href="http://app.finance.ifeng.com/data/stock/tab_zjlx.php?code=sh600018" target="_blank" style="font-weight: normal">资金</a> <a href="http://star.finance.ifeng.com/stock/sh600018" target="_blank" style="font-weight: normal">研报</a>]</span>、光大证券、<a href="http://finance.ifeng.com/app/hq/stock/sh601333/" target="_blank" title="广深铁路 601333"><span style="color:#004276">广深铁路</span></a><span id="sh601333_hq">[<span class="Ared">5.25%</span> <a href="http://app.finance.ifeng.com/data/stock/tab_zjlx.php?code=sh601333" target="_blank" style="font-weight: normal">资金</a> <a href="http://star.finance.ifeng.com/stock/sh601333" target="_blank" style="font-weight: normal">研报</a>]</span>、<a href="http://finance.ifeng.com/app/hq/stock/sh600064/" target="_blank" title="南京高科 600064"><span style="color:#004276">南京高科</span></a><span id="sh600064_hq">[<span class="Ared">1.27%</span> <a href="http://app.finance.ifeng.com/data/stock/tab_zjlx.php?code=sh600064" target="_blank" style="font-weight: normal">资金</a> <a href="http://star.finance.ifeng.com/stock/sh600064" target="_blank" style="font-weight: normal">研报</a>]</span>、<a href="http://finance.ifeng.com/app/hq/stock/sh600316/" target="_blank" title="洪都航空 600316"><span style="color:#004276">洪都航空</span></a><span id="sh600316_hq">[<span class="Ared">1.06%</span> <a href="http://app.finance.ifeng.com/data/stock/tab_zjlx.php?code=sh600316" target="_blank" style="font-weight: normal">资金</a> <a href="http://star.finance.ifeng.com/stock/sh600316" target="_blank" style="font-weight: normal">研报</a>]</span>、<a href="http://finance.ifeng.com/app/hq/stock/sh601608/" target="_blank" title="中信重工 601608"><span style="color:#004276">中信重工</span></a><span id="sh601608_hq">[<span class="Ared">10.07%</span> <a href="http://app.finance.ifeng.com/data/stock/tab_zjlx.php?code=sh601608" target="_blank" style="font-weight: normal">资金</a> <a href="http://star.finance.ifeng.com/stock/sh601608" target="_blank" style="font-weight: normal">研报</a>]</span>前十大流通股东，分别持有市值6.63亿元、2.63亿元、2.33亿元、1.13亿元、0.91亿元、0.71亿元、0.56亿元。</p><p class="detailPic"><img src="/backoffice/upload/image/20160401/1459483185698080003.jpg" alt=""/></p><p class="picIntro">图</p><p>凤山投资这一平台持有的上市公司<a href="http://auto.ifeng.com/hangye/" target="_blank"><span style="color:#004276">行业</span></a>遍布非银金融、通信、机械设备、交通运输、房地产、国防军工，市盈率最低为9.76倍，最高达到382.35倍。</p><p>坤藤投资共进入两家上市公司前十大流通股东，分别为中国联通和光大证券，持有市值分别为6.22亿元和2.19亿元。</p><p class="detailPic"><img src="/backoffice/upload/image/20160401/1459483185738027255.jpg" alt=""/></p><p class="picIntro">图</p><p><strong>增持背景</strong><strong>之</strong><strong>谜</strong></p><p>从名字上看，国家外汇管理局应该负责的是与国家外汇相关的业务，为何突然大举杀入A股？笔者从外汇局官方网站上找到了依据。</p><p>如下图所示，外汇局共承担了10条基本职能，其中前九条均与外汇管理相关，但最后一条却清晰的说明，外汇局还承担这国务院和央行交办的其他事宜。</p><p class="detailPic"><img src="/backoffice/upload/image/20160401/1459483185784082892.jpg" alt=""/></p><p class="picIntro">图</p><p>这从侧面说明，从法理和具体职能上，经国务院或者央行的同意，外汇局的确可以入市增持A股，尤其是在去年A股流动性缺失的关键节点。</p><p>实际上，<strong>在去年11月11日，梧桐树投资还和中国进出口银行共同投资了一家中非产能合作基金有限责任公司，注册资本达到640.85亿元，投资范围为境内外项目、股权、债权、基金、贷款投资；资产受托管理、投资管理。</strong>而去年6月成立，注册资本达到611.32亿元的中拉产能合作投资基金有限责任公司，梧桐树投资也为其中的一个重要股东。</p><p>此外，作为国家外汇管理局的全资子公司，梧桐树投资在去年8月份分别向国家开发银行和中国进出口银行注资480亿<a href="http://app.finance.ifeng.com/hq/rmb/quote.php?symbol=USD" target="_blank"><span style="color:#004276">美元</span></a>和450美元，还以股东身份参与组建了丝路基金。</p><p><strong>三大平台全景扫描</strong></p><p><strong>01</strong>梧\n桐树投资平台有限责任公司梧桐树投资成立于2014年11月5日，为国家外汇管理局独资子公司，注册资本1亿元。经营范围为：境内外项目、股权、债权、基\n金、贷款投资；资产受托管理、投资管理。（依法须经批准的项目，经相关部门批准后依批准的内容开展经营活动。）值得注意的是，梧桐树投资掌舵人的履历颇为\n靓丽。法定代表人、执行董事何建雄研究生学历，毕业于中国对外经济贸易大学。历任中国人民银行国际司副司长；长期担任中国派驻国际货币基金组织副执行董\n事；2009年5月至2011年9月，担任中国驻国际货币基金组织（IMF）执行董事（副部级）。作为梧桐树投资的总经理，刘薇的公开资料不多，只是在\n2014年1月23日,中国银行业协会外资银行工作委员会成员单位高管拜访国家外汇管理局并与人民银行座谈上，国家外汇管理局综合司刘薇司长通报了国家外\n汇管理局近期的工作内容。</p><p><strong>02</strong>北京坤藤投资有限责任公司</p><p>作\n为梧桐树投资全资子公司，坤藤投资成立于2015年8月14日，注册资本达到5000万元。经营范围资产管理；投资管理。不过，未经有关部门批准，不得以\n公开方式募集资金；不得公开开展证券类产品和金融衍生品交易活动；不得发放贷款；不得对所投资企业以外的其他企业提供担保；不得向投资者承诺投资本金不受\n损失或者承诺最低收益”；依法须经批准的项目，经相关部门批准后依批准的内容开展经营活动。据悉，坤藤投资法定代表人、执行董事程昊2006年7月加入国\n家外汇管理局中央外汇业务中心，拥有中国注册会计师、<a href="http://app.travel.ifeng.com/country_76" target="_blank"><span style="color:#004276">澳大利亚</span></a>注册会计师、特许管理会计师等资格。</p><p><br/></p>', '2016-04-01 11:59:47', 50, '梧桐树概念股已增至11只 外汇局大举入市答案在这儿', b'0', b'1', b'0', 250, 242, '', '', ''),
('<p>过半上市券商2015年年报已经披露，其中包括<a href="http://finance.ifeng.com/app/hq/stock/sh600030/" target="_blank" title="中信证券 600030"><span style="color:#004276">中信证券</span></a><span id="sh600030_hq">[<span class="Agreen">-2.81%</span> <a href="http://app.finance.ifeng.com/data/stock/tab_zjlx.php?code=sh600030" target="_blank" style="font-weight: normal">资金</a> <a href="http://star.finance.ifeng.com/stock/sh600030" target="_blank" style="font-weight: normal">研报</a>]</span>、<a href="http://finance.ifeng.com/app/hq/stock/sh600837/" target="_blank" title="海通证券 600837"><span style="color:#004276">海通证券</span></a><span id="sh600837_hq">[<span class="Agreen">-1.05%</span> <a href="http://app.finance.ifeng.com/data/stock/tab_zjlx.php?code=sh600837" target="_blank" style="font-weight: normal">资金</a> <a href="http://star.finance.ifeng.com/stock/sh600837" target="_blank" style="font-weight: normal">研报</a>]</span>等大型券商。除了净利润普遍同比大增之外，作为证券市场的深度参与者，上市券商各项财务数据透露出更多信息，勾勒出去年下半年证券市场的跌宕起伏。</p><p>大多数上市券商虽然去年业绩大增，但巨额净利未能在自己手中停留太久，而是投入证金专户用于救市。报表细节则显示，上市券商去年分两批向证金专户投入的资金截至去年年底浮亏约5%，好于同期大盘走势及市场预期。</p><p><strong>去年下半年忙花钱</strong></p><p>海\n通证券日前披露的2015年年报显示，其在去年实现净利润158.3亿元，较2014年同比大幅增长105.42%；实现营业收入380.8亿元，同比增\n长111.84%。较早披露的中信证券年报显示，其在去年实现净利润197.99亿元，较2014年同比增长74.64%；实现营业收入560亿元，同比\n增长91.84%。</p><p>不过，同比大增的净利润和营业收入对券商来说可谓来得快去得也快。数据显示，多家券商现金流情况去年下半年出现大幅净流出，与去年上半年大相径庭。</p><p>以最近发布年报的<a href="http://finance.ifeng.com/app/hq/stock/sh601377/" target="_blank" title="兴业证券 601377"><span style="color:#004276">兴业证券</span></a><span id="sh601377_hq">[<span class="Agreen">-2.50%</span> <a href="http://app.finance.ifeng.com/data/stock/tab_zjlx.php?code=sh601377" target="_blank" style="font-weight: normal">资金</a> <a href="http://star.finance.ifeng.com/stock/sh601377" target="_blank" style="font-weight: normal">研报</a>]</span>为例，其去年上半年经营活动产生的现金流量净额为168.45亿元，全年则逆转为净流出13.8亿元。从现金流量细节来看，其用于“购置以公允价值计量且其变动计入当期损益的<a href="http://auto.ifeng.com/news/finance/" target="_blank"><span style="color:#004276">金融</span></a>资产”一项的支出下半年激增，上半年该项数字为3.1亿元，而下半年为73亿元。与此相对应的，其年末“以公允价值计量且其变动计入当期损益的金融资产”期末余额也较年中大幅增长，由154.5亿增长至234.8亿。</p><p>同样的现象也出现海通证券，其此年报显示，其全年经营活动现金流量净额为155.8亿元，而其上半年该项为580.1亿元。其全年买入“以公允价值计量且其变动计入当期损益的金融资产”支出为233.3亿元，而其上半年则通过处置金融资产获得现金净流入18.8亿元。</p><p>这些数据显示，在二级市场如火如荼的去年上半年，券商自营部门较为冷静，并未盲目追高，相反在高位处置了不少金融资产，在年中手握大笔现金。而在去年下半年，券商一方面积极参与救市，一方面买入大量债券参与债市牛市，自营股票、债券仓位都大幅提升。</p><p><strong>救市资金小幅浮亏</strong></p><p>去年7月及9月，向证金公司专户注资也是券商去年的一项大宗现金流出。多家上市券商都在年报中披露了其向证金公司专户注资的具体时间及规模。海通证券就披露，根据公司与证金公司签订的相关合同，公司分别于2015 年7 月6 日和2015 年9 月1 日出资<a href="http://app.finance.ifeng.com/hq/rmb/list.php" target="_blank"><span style="color:#004276">人民币</span></a>150 亿元和<a href="http://app.finance.ifeng.com/hq/rmb/list.php" target="_blank"><span style="color:#004276">人民币</span></a>44.57 亿元投入该专户。该专户由公司与其他投资该专户的证券公司按投资比例分担投资风险和分享投资收益，由证金公司进行统一运作与投资管理。其他参与注资的券商年报中的相关内容除出资额度不同外，表述基本相同。</p><p>不同券商根据自身情况，将证金注资列入可供出售金融资产中的不同子项。少数券商将其单列为一个子项，从中也可以窥见证金救市账户去年下半年的盈亏情况。</p><p><a href="http://finance.ifeng.com/app/hq/stock/sz000776/" target="_blank" title="广发证券 000776"><span style="color:#004276">广发证券</span></a><span id="sz000776_hq">[<span class="Agreen">-2.51%</span> <a href="http://app.finance.ifeng.com/data/stock/tab_zjlx.php?code=sz000776" target="_blank" style="font-weight: normal">资金</a> <a href="http://star.finance.ifeng.com/stock/sz000776" target="_blank" style="font-weight: normal">研报</a>]</span>年报就显示，公司分两次合计出资138.63亿元交由中国证券金融公司专项用于蓝筹ETF等投资。截至12月底，这部分专项投资的账面价值为131.77亿元，较初始账面价值减少6.86亿元，减值率为5%。</p><p>值得一提的是，券商不约而同地将此类资金归在资产负债表下可供出售金融资产这一科目里，而非以公允价值变动计入当期损益的金融资产。</p><p>业内人士表示，可供出售金融资产处置的决策过程无法预测，持有时间通常大于一年，因而归于这一科目下的资产的公允价值变动暂时不体现在利润表，而是计入所有者权益，对公司短期盈利状况并无影响。</p><p><strong>计提坏账防两融风险</strong></p><p>受益于去年上半年行情上涨，券商融资业务井喷。中国证券业协会数据显示，全<a href="http://auto.ifeng.com/hangye/" target="_blank"><span style="color:#004276">行业</span></a>利息及佣金收入合计1576.36亿元，在营业收入中占比达到27.41%。</p><p>然而，这样的高景气度或难以长期维系。截至3月29日全市场两融余额较去年年底已减少逾25%，与去年巅峰期相比，更是削去六成多。两融规模锐减的同时，券商也开始担忧背后潜藏的风险。</p><p><a href="http://finance.ifeng.com/app/hq/stock/sh601688/" target="_blank" title="华泰证券 601688"><span style="color:#004276">华泰证券</span></a><span id="sh601688_hq">[<span class="Agreen">-1.75%</span> <a href="http://app.finance.ifeng.com/data/stock/tab_zjlx.php?code=sh601688" target="_blank" style="font-weight: normal">资金</a> <a href="http://star.finance.ifeng.com/stock/sh601688" target="_blank" style="font-weight: normal">研报</a>]</span>去年对融资融券融出资金计提减值准备5977万元，而在2014年该科目计提金额为零。海通证券也在今年增加了融出资金的减值准备，由年初的1.2亿元增至1.4亿元。</p><p>沪上大型券商资深两融人士坦言，去年下半年股市剧烈震荡，少数客户未能如期、足额还款或还券，因此券商按形成的应收账款金额专项计提坏账准备。</p><p>更\n有券商为提高融资类业务防御风险的能力而变更会计估值。国投安信年报中披露，除了专项计提坏账准备，公司还对未计提专项坏账准备的融资类业务，根据融资类\n业务资产分类，按照资产负债表日融资余额的0.3%计提坏账准备。年报显示，公司去年对融资资金、融出证券合计减值准备8334.29万元。\nTHE_END</p><p><br/></p>', '2016-04-01 12:00:20', 50, '券商年报抖料：证金专户浮亏5% 两融业务现坏账', b'0', b'1', b'0', 251, 242, '', '', '');

-- --------------------------------------------------------

--
-- 表的结构 `notify`
--

CREATE TABLE `notify` (
  `id` int(11) NOT NULL,
  `content` text,
  `create_time` datetime NOT NULL,
  `notify_id` int(11) DEFAULT NULL,
  `notify_type` varchar(255) DEFAULT NULL,
  `is_read` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否置顶：1=true=置顶，0=false=不置顶',
  `admins_id` int(11) DEFAULT NULL,
  `users_id` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `rebates`
--

CREATE TABLE `rebates` (
  `id` int(11) NOT NULL,
  `description` varchar(255) NOT NULL,
  `name` varchar(50) NOT NULL,
  `sort_num` int(11) NOT NULL,
  `sql_where` longtext NOT NULL,
  `updated_time` datetime NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `rebate_agents`
--

CREATE TABLE `rebate_agents` (
  `id` int(11) NOT NULL,
  `money` double NOT NULL,
  `updated_time` datetime NOT NULL,
  `rebate_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `rebate_levels`
--

CREATE TABLE `rebate_levels` (
  `id` int(11) NOT NULL,
  `level` int(11) NOT NULL,
  `money` double NOT NULL,
  `updated_time` datetime NOT NULL,
  `rebate_id` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `rebate_records`
--

CREATE TABLE `rebate_records` (
  `id` int(11) NOT NULL,
  `amount` double NOT NULL,
  `comment` varchar(200) NOT NULL,
  `creat_time` datetime NOT NULL,
  `login` int(11) DEFAULT NULL,
  `mt4_order` int(11) DEFAULT NULL,
  `rebate_name` varchar(30) NOT NULL,
  `send` bit(1) NOT NULL DEFAULT b'0',
  `volume` int(11) NOT NULL,
  `order_user_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `rebate_referrals`
--

CREATE TABLE `rebate_referrals` (
  `id` int(11) NOT NULL,
  `money1` double NOT NULL,
  `money2` double NOT NULL,
  `updated_time` datetime NOT NULL,
  `rebate_id` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `rebate_scheme_items`
--

CREATE TABLE `rebate_scheme_items` (
  `id` int(11) NOT NULL,
  `action` int(11) NOT NULL,
  `level` int(11) DEFAULT NULL,
  `rate` double NOT NULL,
  `updated_time` datetime NOT NULL,
  `group_id` int(11) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `rebate_vip_grades`
--

CREATE TABLE `rebate_vip_grades` (
  `id` int(11) NOT NULL,
  `money` double NOT NULL,
  `updated_time` datetime NOT NULL,
  `vip_grade` int(11) NOT NULL,
  `rebate_id` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `settings`
--

CREATE TABLE `settings` (
  `key` varchar(50) NOT NULL,
  `description` varchar(100) NOT NULL,
  `double_value` double NOT NULL,
  `groups` varchar(50) NOT NULL,
  `int_value` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  `string_value` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `updated_time` datetime DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `tokens`
--

CREATE TABLE `tokens` (
  `uuid` varchar(255) NOT NULL,
  `creat_time` datetime NOT NULL,
  `expiration_time` datetime DEFAULT NULL,
  `ip_address` varchar(50) NOT NULL,
  `last_authorization_time` datetime DEFAULT NULL,
  `admin_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `transfers`
--

CREATE TABLE `transfers` (
  `id` int(11) NOT NULL,
  `amount` double NOT NULL,
  `create_time` datetime NOT NULL,
  `currency_type` varchar(16) NOT NULL,
  `from_login` int(11) NOT NULL,
  `to_login` int(11) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `users`
--

CREATE TABLE `users` (
  `id` int(10) UNSIGNED NOT NULL,
  `state` varchar(20) NOT NULL COMMENT '状态',
  `is_frozen` tinyint(1) NOT NULL DEFAULT '0' COMMENT '冻结flag',
  `email` varchar(50) NOT NULL COMMENT '电子邮箱',
  `mobile` varchar(20) NOT NULL COMMENT '手机号',
  `password` varchar(32) NOT NULL COMMENT '密码',
  `payment_password` varchar(32) NOT NULL COMMENT '操作密码',
  `salty` varchar(6) NOT NULL COMMENT '密码盐',
  `registration_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_logon_time` timestamp NULL DEFAULT NULL,
  `up_id` int(10) UNSIGNED DEFAULT NULL COMMENT '上级ID',
  `serial_number` int(10) UNSIGNED NOT NULL COMMENT '属于同一上级的用户的序号',
  `level` int(10) UNSIGNED NOT NULL COMMENT '级别(0总公司,1分公司,2经纪人,3客户,4客户的客户,5...)',
  `group_id` int(10) UNSIGNED DEFAULT NULL,
  `school` varchar(50) NOT NULL,
  `campus` varchar(50) DEFAULT NULL,
  `allocation` bit(1) DEFAULT b'0',
  `allow_deposit` bit(1) NOT NULL DEFAULT b'1',
  `allow_withdrawal` bit(1) NOT NULL DEFAULT b'1',
  `apply_agent` bit(1) NOT NULL DEFAULT b'0',
  `disable` bit(1) NOT NULL,
  `path` longtext,
  `pwd_error_count` int(11) NOT NULL DEFAULT '0' COMMENT '输错密码次数',
  `pwd_fisrt_error_time` datetime DEFAULT NULL,
  `pwd_locked` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否是由于输错密码被锁定',
  `referral_code` varchar(5) DEFAULT NULL,
  `tags` varchar(15) DEFAULT '',
  `vip_grade` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `users`
--

INSERT INTO `users` (`id`, `state`, `is_frozen`, `email`, `mobile`, `password`, `payment_password`, `salty`, `registration_time`, `last_logon_time`, `up_id`, `serial_number`, `level`, `group_id`, `school`, `campus`, `allocation`, `allow_deposit`, `allow_withdrawal`, `apply_agent`, `disable`, `path`, `pwd_error_count`, `pwd_fisrt_error_time`, `pwd_locked`, `referral_code`, `tags`, `vip_grade`) VALUES
(1, 'VERIFIED', 0, 'company@gctexchange.com', '13012345678', '3726e04539e0038fb063ace8d5157a68', '95d3f194236599eb7daf74d9a7ae0ede', 'cf6j78', '2016-03-16 03:51:18', NULL, NULL, 1, 0, 1, '', NULL, b'0', b'1', b'1', b'0', b'0', NULL, 0, NULL, b'0', NULL, '', 0),
(2, 'UNVERIFIED', 0, 'branch@gctexchange.com', '13011111111', 'a316e170e392955ad1fb59ad3b52d02e', '77e31ac2656ba51673d31f096dab4798', 'dde3v3', '2016-03-16 07:07:34', NULL, 1, 1, 1, 1, '', NULL, b'0', b'1', b'1', b'0', b'0', NULL, 0, NULL, b'0', NULL, '', 0),
(3, 'VERIFIED', 0, '843268483@qq.com', '18200240230', 'f9f0b17a199bbc6854f7f2caf3a87f12', 'f9f0b17a199bbc6854f7f2caf3a87f12', 'cf6j78', '2016-03-22 07:14:36', NULL, NULL, 1, 0, 2, '', NULL, b'0', b'1', b'1', b'0', b'0', NULL, 0, NULL, b'0', NULL, '', 0),
(37, 'UNVERIFIED', 0, '316691717@qq.com', '18300001111', '7294e8fddb08d1e6699a952b705d6a52', '7294e8fddb08d1e6699a952b705d6a52', '3m3110', '2016-03-29 07:17:13', NULL, NULL, 1, 3, NULL, '', NULL, b'0', b'1', b'1', b'0', b'0', NULL, 0, NULL, b'0', NULL, '', 0),
(42, 'AUDITING', 0, 'sdfa@qq.com', '18200240237', 'cae28573ac67cb98ab288a3e9b6137b4', 'cae28573ac67cb98ab288a3e9b6137b4', '22nV2j', '2016-03-30 09:12:50', NULL, NULL, 1, 3, NULL, '', NULL, b'0', b'1', b'1', b'0', b'0', NULL, 0, NULL, b'0', NULL, '', 0),
(43, 'AUDITING', 0, '843268481@qq.com', '18200230230', 'a20865cf9fa04b9635b611fcafaa8e5c', 'a20865cf9fa04b9635b611fcafaa8e5c', 'Ihv3pp', '2016-03-30 09:23:09', NULL, NULL, 1, 3, NULL, '', NULL, b'0', b'1', b'1', b'0', b'0', NULL, 0, NULL, b'0', NULL, '', 0);

-- --------------------------------------------------------

--
-- 表的结构 `users_simulation`
--

CREATE TABLE `users_simulation` (
  `id` int(11) NOT NULL,
  `email` varchar(50) NOT NULL,
  `mobile` varchar(20) NOT NULL,
  `password` varchar(32) NOT NULL,
  `registration_time` datetime NOT NULL,
  `salty` varchar(6) NOT NULL,
  `state` varchar(255) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `user_questions`
--

CREATE TABLE `user_questions` (
  `id` int(11) NOT NULL,
  `creat_time` datetime NOT NULL,
  `done_time` datetime DEFAULT NULL,
  `questionContext` longtext NOT NULL,
  `question_type` varchar(20) NOT NULL,
  `state` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `user_name` varchar(100) NOT NULL,
  `mobile` varchar(20) NOT NULL,
  `user_id` int(11) UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `user__balances`
--

CREATE TABLE `user__balances` (
  `id` int(10) UNSIGNED NOT NULL,
  `user_id` int(10) UNSIGNED NOT NULL,
  `currency_type` varchar(3) NOT NULL COMMENT '币种',
  `amount_available` decimal(10,2) NOT NULL,
  `amount_frozen` decimal(10,2) NOT NULL,
  `updated_time` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户余额表';

--
-- 转存表中的数据 `user__balances`
--

INSERT INTO `user__balances` (`id`, `user_id`, `currency_type`, `amount_available`, `amount_frozen`, `updated_time`) VALUES
(2, 1, 'USD', '100.00', '0.00', '2016-03-22 12:47:12'),
(3, 37, 'USD', '99900.00', '0.00', '2016-03-30 09:53:21');

-- --------------------------------------------------------

--
-- 表的结构 `user__balance_logs`
--

CREATE TABLE `user__balance_logs` (
  `id` int(10) UNSIGNED NOT NULL,
  `amount_available` double NOT NULL,
  `amount_frozen` double NOT NULL,
  `creat_time` datetime NOT NULL,
  `currency_type` varchar(3) NOT NULL,
  `deposit_id` int(11) NOT NULL,
  `description` varchar(100) NOT NULL,
  `withdrawal_id` int(11) NOT NULL,
  `user_id` int(10) UNSIGNED NOT NULL,
  `amount` double NOT NULL,
  `transfers_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `user__balance_logs`
--

INSERT INTO `user__balance_logs` (`id`, `amount_available`, `amount_frozen`, `creat_time`, `currency_type`, `deposit_id`, `description`, `withdrawal_id`, `user_id`, `amount`, `transfers_id`) VALUES
(11, 100, 0, '2016-03-22 12:47:12', 'USD', 0, '入金成功', 0, 1, 0, 0),
(12, 100000, 0, '2016-03-30 09:50:44', 'USD', 18, '用户入金', 0, 37, 100000, 0),
(13, 99900, 0, '2016-03-30 09:53:21', 'USD', 0, '用户出金', 1, 37, -100, 0);

-- --------------------------------------------------------

--
-- 表的结构 `user__bank_accounts`
--

CREATE TABLE `user__bank_accounts` (
  `id` int(11) NOT NULL,
  `account_name` varchar(100) NOT NULL,
  `account_no` varchar(50) NOT NULL,
  `bank_address` varchar(93) DEFAULT NULL,
  `bank_branch` varchar(35) DEFAULT NULL,
  `bank_name` varchar(35) DEFAULT NULL,
  `country_code` varchar(2) DEFAULT NULL,
  `currency_type` varchar(3) DEFAULT NULL,
  `intermediary_bank_address` varchar(93) DEFAULT NULL,
  `intermediary_bank_bic_swift_code` varchar(11) DEFAULT NULL,
  `intermediary_bank_branch` varchar(35) DEFAULT NULL,
  `intermediary_bank_name` varchar(35) DEFAULT NULL,
  `sort_num` int(11) DEFAULT NULL,
  `state` bit(1) DEFAULT NULL,
  `swift_code` varchar(11) DEFAULT NULL,
  `user_id` int(11) UNSIGNED NOT NULL,
  `comment` longtext,
  `iban_code` varchar(128) NOT NULL DEFAULT '',
  `isdefault` bit(1) NOT NULL DEFAULT b'0',
  `update_time` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `user__bank_accounts`
--

INSERT INTO `user__bank_accounts` (`id`, `account_name`, `account_no`, `bank_address`, `bank_branch`, `bank_name`, `country_code`, `currency_type`, `intermediary_bank_address`, `intermediary_bank_bic_swift_code`, `intermediary_bank_branch`, `intermediary_bank_name`, `sort_num`, `state`, `swift_code`, `user_id`, `comment`, `iban_code`, `isdefault`, `update_time`) VALUES
(12, '成都银行', '512701545545521', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, b'1', NULL, 43, NULL, '', b'0', '0000-00-00 00:00:00');

-- --------------------------------------------------------

--
-- 表的结构 `user__logs`
--

CREATE TABLE `user__logs` (
  `id` int(10) UNSIGNED NOT NULL,
  `user_id` int(10) UNSIGNED NOT NULL,
  `creat_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_address` varchar(50) NOT NULL COMMENT 'IP地址',
  `action` varchar(50) NOT NULL COMMENT '动作',
  `description` text NOT NULL COMMENT '描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `user__password_resets`
--

CREATE TABLE `user__password_resets` (
  `id` int(10) UNSIGNED NOT NULL,
  `state` tinyint(3) UNSIGNED NOT NULL DEFAULT '0' COMMENT '状态(等待邮件确认,已重设,已取消)',
  `user_id` int(10) UNSIGNED NOT NULL,
  `verify_code` varchar(36) NOT NULL COMMENT '验证码',
  `creat_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `verified_time` timestamp NULL DEFAULT NULL COMMENT '验证重设密码时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='客户重设密码表';

-- --------------------------------------------------------

--
-- 表的结构 `user__profiles`
--

CREATE TABLE `user__profiles` (
  `id` int(11) NOT NULL,
  `user_comname` varchar(100) DEFAULT NULL,
  `user_ename` varchar(100) NOT NULL,
  `user_email` varchar(100) NOT NULL,
  `user_esidentialaddress` varchar(100) NOT NULL,
  `user_idcard` varchar(100) NOT NULL,
  `user_industry` varchar(100) DEFAULT NULL,
  `user_name` varchar(100) NOT NULL,
  `user_nationality` varchar(100) NOT NULL,
  `user_phone` varchar(100) NOT NULL,
  `user_res_time` varchar(100) NOT NULL,
  `user_trading_Long` varchar(100) DEFAULT NULL,
  `user_update_time` varchar(100) DEFAULT NULL,
  `user_years_income` varchar(100) DEFAULT NULL,
  `user_id` int(11) UNSIGNED NOT NULL,
  `back_card_type` varchar(100) DEFAULT NULL,
  `back_no` varchar(100) DEFAULT NULL,
  `card_type` varchar(100) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `company` varchar(100) DEFAULT NULL,
  `position` varchar(100) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `comment` longtext,
  `ename` varchar(100) DEFAULT NULL,
  `esidentialaddress` varchar(100) NOT NULL,
  `idcard` varchar(100) NOT NULL,
  `industry` varchar(100) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `nationality` varchar(100) NOT NULL,
  `years_income` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `user__profiles`
--

INSERT INTO `user__profiles` (`id`, `user_comname`, `user_ename`, `user_email`, `user_esidentialaddress`, `user_idcard`, `user_industry`, `user_name`, `user_nationality`, `user_phone`, `user_res_time`, `user_trading_Long`, `user_update_time`, `user_years_income`, `user_id`, `back_card_type`, `back_no`, `card_type`, `create_time`, `company`, `position`, `update_time`, `comment`, `ename`, `esidentialaddress`, `idcard`, `industry`, `name`, `nationality`, `years_income`) VALUES
(14, '', 'helei', '843268481@qq.com', '成都市武侯区', '513701199307306513', '', '何磊', '中国', '18200230230', '2016-03-30 17:23:08.642', '', NULL, '', 43, NULL, NULL, '身份证', NULL, NULL, NULL, NULL, NULL, NULL, '', '', NULL, '', '', NULL);

-- --------------------------------------------------------

--
-- 表的结构 `withdrawals`
--

CREATE TABLE `withdrawals` (
  `id` int(10) UNSIGNED NOT NULL,
  `account_name` varchar(35) NOT NULL,
  `account_number` varchar(30) NOT NULL,
  `address1` varchar(31) NOT NULL,
  `address2` varchar(31) NOT NULL,
  `address3` varchar(31) NOT NULL,
  `amount` double NOT NULL,
  `audited_memo` varchar(255) NOT NULL,
  `audited_time` datetime DEFAULT NULL,
  `bank_name` varchar(35) NOT NULL,
  `bank_reference` varchar(50) NOT NULL,
  `branch` varchar(31) NOT NULL,
  `canceled_time` datetime DEFAULT NULL,
  `country` varchar(50) NOT NULL,
  `creat_time` datetime NOT NULL,
  `currency` varchar(3) NOT NULL,
  `date_time` datetime DEFAULT NULL,
  `expiry_date` date DEFAULT NULL,
  `intermediary_bank_bic_swift_code` varchar(11) NOT NULL,
  `intermediary_bank_branch` varchar(35) NOT NULL,
  `intermediary_bank_name` varchar(35) NOT NULL,
  `internal_reference` varchar(50) NOT NULL,
  `postcode` varchar(50) NOT NULL,
  `remittance` varchar(200) NOT NULL,
  `sender_reference` varchar(50) NOT NULL,
  `state` varchar(20) NOT NULL,
  `type` varchar(50) NOT NULL,
  `user_memo` varchar(255) NOT NULL,
  `user_id` int(10) UNSIGNED NOT NULL,
  `swift_code` varchar(11) NOT NULL,
  `bank_address` varchar(128) NOT NULL DEFAULT '',
  `bank_branch` varchar(50) NOT NULL DEFAULT '',
  `exchange_rate` double DEFAULT '6.6582',
  `iban_code` varchar(128) NOT NULL DEFAULT '',
  `user_comment` longtext
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `withdrawals`
--

INSERT INTO `withdrawals` (`id`, `account_name`, `account_number`, `address1`, `address2`, `address3`, `amount`, `audited_memo`, `audited_time`, `bank_name`, `bank_reference`, `branch`, `canceled_time`, `country`, `creat_time`, `currency`, `date_time`, `expiry_date`, `intermediary_bank_bic_swift_code`, `intermediary_bank_branch`, `intermediary_bank_name`, `internal_reference`, `postcode`, `remittance`, `sender_reference`, `state`, `type`, `user_memo`, `user_id`, `swift_code`, `bank_address`, `bank_branch`, `exchange_rate`, `iban_code`, `user_comment`) VALUES
(1, '何磊', '545454824555145524', '', '', '', 100, '', NULL, '成都银行', '', '', NULL, 'AO', '2016-03-30 09:53:21', 'USD', NULL, NULL, '', '', '', '', '', '', '', 'WAITING', '', '', 37, '', '', '', 6.6582, '', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admins`
--
ALTER TABLE `admins`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `account` (`account`),
  ADD KEY `state` (`role`);

--
-- Indexes for table `admin__logs`
--
ALTER TABLE `admin__logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`admin_id`),
  ADD KEY `creat_time` (`creat_time`),
  ADD KEY `action` (`action`);

--
-- Indexes for table `announcement`
--
ALTER TABLE `announcement`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_8repo67643plo0q3d9jbo95oe` (`admins_id`);

--
-- Indexes for table `archives`
--
ALTER TABLE `archives`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `attachments`
--
ALTER TABLE `attachments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `creat_time` (`creat_time`),
  ADD KEY `is_image` (`is_image`),
  ADD KEY `owner_type` (`owner_type`,`owner_id`),
  ADD KEY `sort_num` (`sort_num`);

--
-- Indexes for table `cdd_checks`
--
ALTER TABLE `cdd_checks`
  ADD PRIMARY KEY (`id`),
  ADD KEY `admin_id` (`admin_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `deposit_id` (`deposit_id`),
  ADD KEY `withdrawal_id` (`withdrawal_id`),
  ADD KEY `result` (`result`),
  ADD KEY `tag` (`tag`),
  ADD KEY `timestamp` (`timestamp`),
  ADD KEY `FK_gon0nb3eksq9ful9c54r9l760` (`user_bank_account_id`);

--
-- Indexes for table `columns`
--
ALTER TABLE `columns`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `alias` (`alias`),
  ADD KEY `up_id` (`up_id`),
  ADD KEY `up_id_2` (`up_id`,`sort_num`);

--
-- Indexes for table `deposits`
--
ALTER TABLE `deposits`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `email_validation`
--
ALTER TABLE `email_validation`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_o6qsvtchu8avm2gs2s4tv4ifo` (`user_id`);

--
-- Indexes for table `groups`
--
ALTER TABLE `groups`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`),
  ADD KEY `sort_num` (`sort_num`);

--
-- Indexes for table `images`
--
ALTER TABLE `images`
  ADD PRIMARY KEY (`id`),
  ADD KEY `archive_id` (`archive_id`);

--
-- Indexes for table `links`
--
ALTER TABLE `links`
  ADD PRIMARY KEY (`id`),
  ADD KEY `column_id` (`column_id`),
  ADD KEY `creat_time` (`creat_time`),
  ADD KEY `sort_num` (`sort_num`);

--
-- Indexes for table `news`
--
ALTER TABLE `news`
  ADD UNIQUE KEY `id` (`id`),
  ADD KEY `FK_fxavcx12p04p3909qd2wl6o6` (`column_id`);

--
-- Indexes for table `notify`
--
ALTER TABLE `notify`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_pui78clf0q5h8krco7qnh22eb` (`admins_id`),
  ADD KEY `FK_a5yfidyxj9wvp367cora63iuc` (`users_id`);

--
-- Indexes for table `rebates`
--
ALTER TABLE `rebates`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_dwlmlwx9gi2ixylpgwin4023p` (`name`);

--
-- Indexes for table `rebate_agents`
--
ALTER TABLE `rebate_agents`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_1xfqejxl51p1u10fedqrrlcio` (`rebate_id`),
  ADD KEY `FK_4xekmq3ek7fqfu9rtoaelyuqc` (`user_id`);

--
-- Indexes for table `rebate_levels`
--
ALTER TABLE `rebate_levels`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_aa8oq7pq3fy6o8av4qloxnwgh` (`rebate_id`);

--
-- Indexes for table `rebate_records`
--
ALTER TABLE `rebate_records`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_syle577gikvcnd7bx3hc2l32k` (`order_user_id`),
  ADD KEY `FK_f25hragb88k158c3oj5mkjiy` (`user_id`);

--
-- Indexes for table `rebate_referrals`
--
ALTER TABLE `rebate_referrals`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_c2lqe9xyvpuw0e66li5nfcbf` (`rebate_id`);

--
-- Indexes for table `rebate_scheme_items`
--
ALTER TABLE `rebate_scheme_items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_l1tdfosxo90a434wwwivp1vbl` (`group_id`);

--
-- Indexes for table `rebate_vip_grades`
--
ALTER TABLE `rebate_vip_grades`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_bytqdlwclvf9a8twtdxp1ivch` (`rebate_id`);

--
-- Indexes for table `settings`
--
ALTER TABLE `settings`
  ADD PRIMARY KEY (`key`);

--
-- Indexes for table `tokens`
--
ALTER TABLE `tokens`
  ADD PRIMARY KEY (`uuid`),
  ADD UNIQUE KEY `UK_il400euf8jvycx5oi7k4dsyrf` (`admin_id`),
  ADD UNIQUE KEY `UK_lgokc3vw1rct83pdwryntacb9` (`user_id`);

--
-- Indexes for table `transfers`
--
ALTER TABLE `transfers`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_jllmes51ovw1obnc3p02e9w26` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `mobile` (`mobile`),
  ADD UNIQUE KEY `up_id_2` (`up_id`,`serial_number`),
  ADD UNIQUE KEY `UK_db3w2sgtsy1kf0qitc77h0bpa` (`referral_code`),
  ADD KEY `registration_time` (`registration_time`),
  ADD KEY `up_id` (`up_id`),
  ADD KEY `state` (`state`),
  ADD KEY `FK_fm4cfgdt24toh89yw4rbnu1lb` (`group_id`);

--
-- Indexes for table `users_simulation`
--
ALTER TABLE `users_simulation`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_mf3drq82ys62esy0aq3i808ux` (`email`),
  ADD UNIQUE KEY `UK_es6iybx4xh30getkv6mdm251u` (`mobile`);

--
-- Indexes for table `user_questions`
--
ALTER TABLE `user_questions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_9w400r5fb0gtl6rfhrfpewg0u` (`user_id`);

--
-- Indexes for table `user__balances`
--
ALTER TABLE `user__balances`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `currency_type` (`currency_type`);

--
-- Indexes for table `user__balance_logs`
--
ALTER TABLE `user__balance_logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_10u5hf1ru6u6q9egu70s0cn6n` (`user_id`);

--
-- Indexes for table `user__bank_accounts`
--
ALTER TABLE `user__bank_accounts`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_23nufsnjho3l9lqxqhcad027y` (`user_id`);

--
-- Indexes for table `user__logs`
--
ALTER TABLE `user__logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `creat_time` (`creat_time`),
  ADD KEY `action` (`action`);

--
-- Indexes for table `user__password_resets`
--
ALTER TABLE `user__password_resets`
  ADD PRIMARY KEY (`id`),
  ADD KEY `creat_time` (`creat_time`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `user__profiles`
--
ALTER TABLE `user__profiles`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_ecsgknj3mci1ph115mr7tf8ae` (`user_id`);

--
-- Indexes for table `withdrawals`
--
ALTER TABLE `withdrawals`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_ie5kdmxwapghgl6wuf3me0f7l` (`user_id`);

--
-- 在导出的表使用AUTO_INCREMENT
--

--
-- 使用表AUTO_INCREMENT `admins`
--
ALTER TABLE `admins`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- 使用表AUTO_INCREMENT `admin__logs`
--
ALTER TABLE `admin__logs`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;
--
-- 使用表AUTO_INCREMENT `announcement`
--
ALTER TABLE `announcement`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- 使用表AUTO_INCREMENT `archives`
--
ALTER TABLE `archives`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=253;
--
-- 使用表AUTO_INCREMENT `attachments`
--
ALTER TABLE `attachments`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=213;
--
-- 使用表AUTO_INCREMENT `cdd_checks`
--
ALTER TABLE `cdd_checks`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;
--
-- 使用表AUTO_INCREMENT `deposits`
--
ALTER TABLE `deposits`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;
--
-- 使用表AUTO_INCREMENT `email_validation`
--
ALTER TABLE `email_validation`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- 使用表AUTO_INCREMENT `groups`
--
ALTER TABLE `groups`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- 使用表AUTO_INCREMENT `images`
--
ALTER TABLE `images`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;
--
-- 使用表AUTO_INCREMENT `notify`
--
ALTER TABLE `notify`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- 使用表AUTO_INCREMENT `rebates`
--
ALTER TABLE `rebates`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- 使用表AUTO_INCREMENT `rebate_agents`
--
ALTER TABLE `rebate_agents`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- 使用表AUTO_INCREMENT `rebate_levels`
--
ALTER TABLE `rebate_levels`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- 使用表AUTO_INCREMENT `rebate_records`
--
ALTER TABLE `rebate_records`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- 使用表AUTO_INCREMENT `rebate_referrals`
--
ALTER TABLE `rebate_referrals`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- 使用表AUTO_INCREMENT `rebate_scheme_items`
--
ALTER TABLE `rebate_scheme_items`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- 使用表AUTO_INCREMENT `rebate_vip_grades`
--
ALTER TABLE `rebate_vip_grades`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- 使用表AUTO_INCREMENT `transfers`
--
ALTER TABLE `transfers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- 使用表AUTO_INCREMENT `users`
--
ALTER TABLE `users`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=44;
--
-- 使用表AUTO_INCREMENT `users_simulation`
--
ALTER TABLE `users_simulation`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- 使用表AUTO_INCREMENT `user_questions`
--
ALTER TABLE `user_questions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- 使用表AUTO_INCREMENT `user__balances`
--
ALTER TABLE `user__balances`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- 使用表AUTO_INCREMENT `user__balance_logs`
--
ALTER TABLE `user__balance_logs`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;
--
-- 使用表AUTO_INCREMENT `user__bank_accounts`
--
ALTER TABLE `user__bank_accounts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;
--
-- 使用表AUTO_INCREMENT `user__logs`
--
ALTER TABLE `user__logs`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;
--
-- 使用表AUTO_INCREMENT `user__password_resets`
--
ALTER TABLE `user__password_resets`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;
--
-- 使用表AUTO_INCREMENT `user__profiles`
--
ALTER TABLE `user__profiles`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;
--
-- 使用表AUTO_INCREMENT `withdrawals`
--
ALTER TABLE `withdrawals`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
--
-- 限制导出的表
--

--
-- 限制表 `admin__logs`
--
ALTER TABLE `admin__logs`
  ADD CONSTRAINT `FK_dqkhsixv51d1f1a0ev6r31itj` FOREIGN KEY (`admin_id`) REFERENCES `admins` (`id`);

--
-- 限制表 `cdd_checks`
--
ALTER TABLE `cdd_checks`
  ADD CONSTRAINT `FK_9ooyb1rknt148b1j8v0mfv2pc` FOREIGN KEY (`deposit_id`) REFERENCES `deposits` (`id`),
  ADD CONSTRAINT `FK_asbglt0hxi0ecxcyv1or63u5k` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FK_gh4c6tf6nuj098r40bnb2lyqv` FOREIGN KEY (`admin_id`) REFERENCES `admins` (`id`),
  ADD CONSTRAINT `FK_gon0nb3eksq9ful9c54r9l760` FOREIGN KEY (`user_bank_account_id`) REFERENCES `user__bank_accounts` (`id`),
  ADD CONSTRAINT `FK_squgb6ec5ogck3sbp9f0lxgu6` FOREIGN KEY (`withdrawal_id`) REFERENCES `withdrawals` (`id`);

--
-- 限制表 `columns`
--
ALTER TABLE `columns`
  ADD CONSTRAINT `FK_1dg63tbrbrrthmf66pkh0huwp` FOREIGN KEY (`id`) REFERENCES `archives` (`id`),
  ADD CONSTRAINT `FK_hqy9c4wk2rx2nhpxtko3t4ipb` FOREIGN KEY (`up_id`) REFERENCES `columns` (`id`);

--
-- 限制表 `deposits`
--
ALTER TABLE `deposits`
  ADD CONSTRAINT `FK_ejfnlhmmie3t4k0cq35kwv2f7` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- 限制表 `images`
--
ALTER TABLE `images`
  ADD CONSTRAINT `FK_6idvrquduwsqukuqfpmg8ku4l` FOREIGN KEY (`archive_id`) REFERENCES `archives` (`id`);

--
-- 限制表 `links`
--
ALTER TABLE `links`
  ADD CONSTRAINT `FK_43mgehfi8x0sqfx1q90ila4w0` FOREIGN KEY (`id`) REFERENCES `archives` (`id`),
  ADD CONSTRAINT `FK_aduy3mj4k6bh6gsctscvbxig6` FOREIGN KEY (`column_id`) REFERENCES `columns` (`id`);

--
-- 限制表 `news`
--
ALTER TABLE `news`
  ADD CONSTRAINT `FK_fxavcx12p04p3909qd2wl6o6` FOREIGN KEY (`column_id`) REFERENCES `columns` (`id`),
  ADD CONSTRAINT `FK_ldnioarhnwy003iyrd8lxiqt0` FOREIGN KEY (`id`) REFERENCES `archives` (`id`);

--
-- 限制表 `rebate_scheme_items`
--
ALTER TABLE `rebate_scheme_items`
  ADD CONSTRAINT `FK_l1tdfosxo90a434wwwivp1vbl` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`);

--
-- 限制表 `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `FK_fm4cfgdt24toh89yw4rbnu1lb` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`),
  ADD CONSTRAINT `FK_n2al381nxmmpurq2wl705w600` FOREIGN KEY (`up_id`) REFERENCES `users` (`id`);

--
-- 限制表 `user_questions`
--
ALTER TABLE `user_questions`
  ADD CONSTRAINT `FK_9w400r5fb0gtl6rfhrfpewg0u` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- 限制表 `user__balances`
--
ALTER TABLE `user__balances`
  ADD CONSTRAINT `FK_niavsvr20wakk9hqcq7le943f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- 限制表 `user__balance_logs`
--
ALTER TABLE `user__balance_logs`
  ADD CONSTRAINT `FK_10u5hf1ru6u6q9egu70s0cn6n` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- 限制表 `user__bank_accounts`
--
ALTER TABLE `user__bank_accounts`
  ADD CONSTRAINT `FK_23nufsnjho3l9lqxqhcad027y` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- 限制表 `user__logs`
--
ALTER TABLE `user__logs`
  ADD CONSTRAINT `FK_t7voh1xr153ltkj682snq2531` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- 限制表 `user__profiles`
--
ALTER TABLE `user__profiles`
  ADD CONSTRAINT `FK_ecsgknj3mci1ph115mr7tf8ae` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- 限制表 `withdrawals`
--
ALTER TABLE `withdrawals`
  ADD CONSTRAINT `FK_ie5kdmxwapghgl6wuf3me0f7l` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
