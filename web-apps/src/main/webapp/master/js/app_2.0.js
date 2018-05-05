'use strict';

/**
 * @author doubeye
 * @ngdoc overview
 * @description
 *
 * Main module of the application.
 */
var app = angular.module('dbUtilsApp2', [
        'ngAnimate',
        'ngAria',
        'ngCookies',
        'ngMessages',
        'ngResource',
        'ngRoute',
        'Routing',
        'ngSanitize',
        'ngMaterial',
        'ui.router',
        'mainFrame',
        'login',
        'welcome',
        'sasrio.angular-material-sidenav',
        'ngDialog',
        'dGroupRadio', 'dDateTimePicker',
        'vote',
        'studentInfo',
        //charts
        'lineChart', 'pieChart', 'scatterChart', 'wordCloud', 'multiDimensionPieChart',
        'dGrid',
        //dataMining commons
        'remarkList',
        //edu dataMining
        'feedbackEnrollCompare', 'localRecordBrowser', 'statByCampus', 'fatalLog', 'uploadLog', 'hotVocabularyManager', 'keywordGroupManager',
        'recordScheduleConsole', 'scheduleRunningLog', 'recordDbEtlLog', 'suspicious', 'recordAnalyzeResultBrowser', 'contribution', 'trend',
        'callOutAvgAnalyze', 'suspiciousV2', 'assignByDayAnalyze',
        //edu dataMining enrollment
        'enrollmentContribution', 'enrollTrend', 'enrollTrendV2', 'enrollConfirm', 'goalManager', 'campusGoalManage', 'campusGoalAchievement',
        'goalAchievement', 'specialtyDayEnrollWarning', 'enrollConversionRate', 'dayEnrollment',
        //edu dataMining assignment
        'assignmentTrend',
        //spider
        'analyzeResult', 'detailResult', 'universitySearch', 'companyList',
        //thematicAnalyze
        'noInvite',
        //component
        'dPager', 'dDateRangePicker', 'dSimpleDateTimePicker',
        //business component
        'term', 'campusTeam',
        //authorization
        'onlineAuth',
        'networkMonitor','serverShellMonitor', 'memCacheMonitor', 'mysqlMonitor',
        'etlCasesList', 'toBespokeInfo', 'privilegeInfo', 'newEduStudentInfo', 'callInfo',
        'assignLog', 'caseList', 'checklist-model', 'changeUserName',
        'dManageableComboBox', 'dComboBox', 'meSignOut', "projectEnvironments",
        'g1b.datetime-range', "angularBootstrapNavTree",  'mysqlDetail',
        'compareJson', 'smart-table', 'eController', 'runningTasks', 'taskManager', 'scheduleEditor'])
    .config([
        '$mdThemingProvider',
        '$locationProvider',
        '$urlRouterProvider',
        '$controllerProvider',
        '$stateProvider',
        'ssSideNavSectionsProvider',
        '$mdIconProvider',
        '$mdDateLocaleProvider',
        function ($mdThemingProvider,
                  $locationProvider,
                  $urlRouterProvider,
                  $controllerProvider,
                  $stateProvider,
                  ssSideNavSectionsProvider, $mdIconProvider, $mdDateLocaleProvider) {
            $mdDateLocaleProvider.formatDate = function(date) {
                return date ? moment(date).format('YYYY-MM-DD') : '';
            };

            $mdDateLocaleProvider.parseDate = function(dateString) {
                var m = moment(dateString, 'YYYY-MM-DD', true);
                return m.isValid() ? m.toDate() : new Date(NaN);
            };
            $mdIconProvider.icon('md-toggle-arrow', 'images/icons/md-toggle-arrow.svg', 48);
            $mdThemingProvider.theme('default').primaryPalette('blue', {'default': '700'});
            ssSideNavSectionsProvider.initWithTheme($mdThemingProvider);
            app.registerController = $controllerProvider.register;
            // 菜单的加载

            var menuItems = [{"menuId":2,"name":"e系统","id":"E","type":"toggle","order":1,"pages":[{"menuId":202,"name":"预约通道信息","id":"to-bespoke-info","state":"toBespokeInfo","type":"link","order":1},{"menuId":211,"name":"专业班招生预警","id":"SPECIALTY-DAY-ENROLL-WARNING","state":"specialtyDayEnrollWarning","type":"link","order":10},{"menuId":212,"name":"校区目标设置","id":"CAMPUS-GOAL-MANAGE","state":"campusGoalManage","type":"link","order":11},{"menuId":213,"name":"校区目标达成","id":"CAMPUS-GOAL-ACHIEVEMENT","state":"campusGoalAchievement","type":"link","order":12},{"menuId":214,"name":"报名转化率","id":"ENROLL-CONVENSION-RATE","state":"enrollConversionRate","type":"link","order":13},{"menuId":215,"name":"二次邀约","id":"NO-INVETE","state":"noInvite","type":"link","order":14},{"menuId":216,"name":"日报名统计表","id":"DAY-ENROLLMENT","state":"dayEnrollment","type":"link","order":15},{"menuId":203,"name":"权限信息（E）","id":"privilege-info","state":"privilegeInfo","type":"link","order":2},{"menuId":204,"name":"学生信息（E）","id":"new-edu-student-info","state":"newEduStudentInfo","type":"link","order":3},{"menuId":205,"name":"分配日志","id":"assign-log","state":"assignLog","type":"link","order":4},{"menuId":206,"name":"电话信息","id":"CALL-INFO","state":"callInfo","type":"link","order":5},{"menuId":207,"name":"报名确认","id":"ENROLL-CONFIRM","state":"enrollConfirm","type":"link","order":6},{"menuId":208,"name":"日均分配报表","id":"ASSIGN-BY-DAY-ANALYZE","state":"assignByDayAnalyze","type":"link","order":7},{"menuId":209,"name":"ADA目标设置","id":"GOAL-MANAGER","state":"goalManager","type":"link","order":8},{"menuId":210,"name":"ADA目标达成","id":"GOAL-ACHIEVEMENT","state":"goalAchievement","type":"link","order":9}]},{"menuId":11,"name":"权限管理","id":"authorization","state":"","type":"toggle","order":10,"pages":[{"menuId":1101,"name":"在线权限管理","id":"ONLINE-AUTH","state":"onlineAuth","type":"link","order":1}]},{"menuId":3,"name":"ME","id":"ME","type":"toggle","order":2},{"menuId":4,"name":"HR","id":"HR","type":"toggle","order":3,"pages":[{"menuId":401,"name":"改变用户名称（HR）","id":"hr-change-user-name","state":"changeUserName","type":"link","order":0}]},{"menuId":5,"name":"通用功能","id":"common","type":"toggle","order":4,"pages":[{"menuId":501,"name":"数据库同步（FEDERATED）","id":"ETL-FEDERATED","state":"etlCasesList","type":"link","order":0},{"menuId":502,"name":"投票","id":"vote","state":"vote","type":"link","order":1},{"menuId":503,"name":"JSON比较","id":"compare-json","state":"compareJson","type":"link","order":2}]},{"menuId":6,"name":"管理","id":"administation","type":"toggle","order":5,"pages":[{"menuId":601,"name":"MySQL数据导入导出","id":"mysql-dump-inport","state":"caseList","type":"link","order":0},{"menuId":602,"name":"项目环境信息","id":"project-environments","state":"projectEnvironments","type":"link","order":1}]},{"menuId":7,"name":"监控","id":"monitor","type":"toggle","order":6,"pages":[{"menuId":701,"name":"MemCache监控","id":"MemCache-Monitor","state":"memCacheMonitor","type":"link","order":0},{"menuId":702,"name":"MySQL监控","id":"MySQL-Monitor","state":"mysqlMonitor","type":"link","order":1},{"menuId":705,"name":"操作系统监控","id":"SERVER-SHELL-MONITOR","state":"serverShellMonitor","type":"link","order":2},{"menuId":703,"name":"Network监控","id":"Network-Monitor","state":"networkMonitor","type":"link","order":3}]},{"menuId":8,"name":"数据抓取","id":"spider","type":"toggle","order":7,"pages":[{"menuId":801,"name":"招聘网站","id":"ANALYZE-RESULT","state":"analyzeResult","type":"link","order":0},{"menuId":802,"name":"高校查询","id":"UNIVERSITY-SEARCH","state":"universitySearch","type":"link","order":1},{"menuId":803,"name":"公司池","id":"COMPANY-LIST","state":"companyList","type":"link","order":3}]},{"menuId":9,"name":"计划任务","id":"schedule","type":"toggle","order":8,"pages":[{"menuId":902,"name":"正在运行的任务","id":"RUNNING-TASKS","state":"runningTasks","type":"link","order":1}]},{"menuId":10,"name":"数据挖掘","id":"dataMining","state":"","type":"toggle","order":9,"pages":[{"menuId":1001,"name":"报名比对","id":"FEEDBACK-ENROLL-COMPARE","state":"feedbackEnrollCompare","type":"link","order":0},{"menuId":1002,"name":"录音转文字","id":"LOCAL-RECORD-BROWSER","state":"localRecordBrowser","type":"link","order":1},{"menuId":1011,"name":"可疑电话","id":"SUSPICIOUS","state":"suspicious","type":"link","order":10},{"menuId":1012,"name":"贡献度","id":"CONTRIBUTION","state":"contribution","type":"link","order":11},{"menuId":1013,"name":"趋势分析","id":"TREND","state":"trend","type":"link","order":12},{"menuId":1014,"name":"日均统计表","id":"CALL-OUT-AVG-ANALYZE","state":"callOutAvgAnalyze","type":"link","order":13},{"menuId":1015,"name":"报名贡献度","id":"ENROLLMENT-CONTRIBUTION","state":"enrollmentContribution","type":"link","order":14},{"menuId":1016,"name":"报名趋势图","id":"ENROLLMENT-TREND","state":"enrollTrend","type":"link","order":15},{"menuId":1017,"name":"报名趋势图第二版","id":"ENROLLMENT-TREND-V2","state":"enrollTrendV2","type":"link","order":16},{"menuId":1018,"name":"可疑电话","id":"SUSPICIOUSV2","state":"suspiciousV2","type":"link","order":17},{"menuId":1019,"name":"分配趋势","id":"ASSIGNMENT-TREND","state":"assignmentTrend","type":"link","order":18},{"menuId":1003,"name":"校区录音统计","id":"STAT-BY-CAMPUS","state":"statByCampus","type":"link","order":2},{"menuId":1004,"name":"异常日志","id":"FATAL-LOG","state":"fatalLog","type":"link","order":3},{"menuId":1005,"name":"上传日志","id":"UPLOAD-LOG","state":"uploadLog","type":"link","order":4},{"menuId":1006,"name":"热词管理","id":"HOT-VOCABULARY-MANAGER","state":"hotVocabularyManager","type":"link","order":5},{"menuId":1007,"name":"关键词组管理","id":"KEYWORD-GROUP-MANAGER","state":"keywordGroupManager","type":"link","order":6},{"menuId":1008,"name":"任务控制台","id":"RECORD-SCHEDULE-CONSOLE","state":"recordScheduleConsole","type":"link","order":7},{"menuId":1009,"name":"计划任务运行日志","id":"SCHEDULE-RUNNING-LOG","state":"scheduleRunningLog","type":"link","order":8},{"menuId":1010,"name":"朗视抽取日志","id":"RECORD-DB-ETL-LOG","state":"recordDbEtlLog","type":"link","order":9}]}];
            menuItems.push({
                munuId: "-1",
                name: "登录",
                id: "login",
                state: "login"
            });

            menuItems.push({
                munuId: "-2",
                name: "主页面",
                id: "main-frame",
                state: "mainFrame"
            });
            menuItems.push({
                munuId: "-3",
                name: "欢迎",
                id: "welcome",
                state: "welcome"
            });

            menuItems.push({
                munuId: "-4",
                name: "mysql监控详情",
                id: "mysql-detail",
                state: "mysqlDetail"
            });

            var generateState = function (items) {
                items.forEach(function (element) {
                    if (element.pages) {
                        generateState(element.pages)
                    }
                    if (element.type !== 'toggle') {
                        $stateProvider.state({
                            name: element.state,
                            url: '/' + element.state,
                            component: element.state
                        });
                    }
                });

            };

            generateState(menuItems);
            $stateProvider.state({
                name : 'forbidden',
                url: '/' + 'forbidden',
                templateUrl : 'app/views/infoPages/403.html'
            });

            $urlRouterProvider.otherwise(function () {
                return '/';
            });
            ssSideNavSectionsProvider.initWithSections(menuItems);

            //routerProvider.setMenuProvider(ssSideNavSectionsProvider);
        }
    ]).run(['$cookies', '$state', '$location', '$transitions', function($cookies, $state, $location, $transitions){
        $transitions.onBefore({ to: '*' }, function(transition, a, b, c) {
            var targetState = transition.to().component ? transition.to().component : transition.to().name;
            if (com.doubeye.Utils.isValueInObjectArray(targetState, com.doubeye.Utils.application.publicStates)) {
                return true;
            } else {
                var menu = com.doubeye.Utils.array.getObjectFromArray({state : targetState}, com.doubeye.Utils.application.flatMenus);
                if (menu) {
                    var cookiePropertyName = $cookies.get("_userId") + "_lastState";
                    $cookies.put(cookiePropertyName, '/' + menu.state    );
                    return true;
                } else if (!com.doubeye.Utils.application.flatMenus || com.doubeye.Utils.application.flatMenus.length === 0) {
                    return true;
                } else {
                    $location.path('/forbidden');
                }

            }
        });
    }]);
app.config(function ($compileProvider) {
    $compileProvider.preAssignBindingsEnabled(true);
});