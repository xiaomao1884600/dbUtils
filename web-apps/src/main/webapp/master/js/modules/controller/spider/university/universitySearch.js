angular.module('universitySearch', []).component('universitySearch', {
    templateUrl : 'app/views/spider/university/universitySearch.html',
    controller : ['httpService', '$mdDialog', function analyzeResultController(httpService, $mdDialog) {
        var self = this;
        this.message = '';
        this.universities = [];
       this.$onInit = function () {

           this.currentPage = 0;
           this.totalPage = 0;
           this.recordPerPage = 10;
           this.totalRecords = 0;

           this.sources = [{
               id : 'universities_gx211',
               name : '中国高校网'
           }, {
                   id : 'universities',
                   name : '高校帮'
           }];
           this.selectedSource = this.sources[0];

           this.cut = function(value) {
               if (value.length > 20) {
                   return value.substr(0, 20) + '...';
               } else {
                   return value;
               }
           };
           this.showMessage = function (info) {
               self.text = info;
               $mdDialog.show({
                   contentElement: '#textBrowser',
                   clickOutsideToClose: true
               });
           };
           this.__columnDefines = [{
               label : '学校名称',
               dataId : 'university'
           }, {
               label : '所在城市',
               dataId : 'city'

           }, {
               label : '联系电话',
               dataId : 'phone'
           }, {
               label : '邮箱',
               dataId : 'email'
           }, {
               label : '专业',
               dataId : 'majors',
               value : self.cut
           }, {
               label : '简介',
               dataId : 'introduction',
               value : self.cut
           }];
           this.__operations = [{
               text : '专业详情',
               callback : function(record) {
                   self.showMessage(record.majors);
               }
           }, {
               text : '简介详情',
               callback : function(record) {
                   self.showMessage(record.introduction);
               }
           }];

           this.doSearch = function(start, end) {
               httpService.sendRequest(self, 'generalRouter', {
                   objectName : 'com.doubeye.spider.content.analyzer.university.gaoKaoPai.service.SearchService',
                   action : 'fullTextSearch',
                   keyword : self.keyword,
                   city : self.city,
                   start : start,
                   source : self.selectedSource.id,
                   size : self.recordPerPage
               }, {
                   dataPropertyName : 'universities',
                   callback : function (data, customParams, totalRecords) {
                       self.totalRecords = totalRecords;
                       if (totalRecords > 0) {
                           self.currentPage = Math.ceil((start + 1) / self.recordPerPage);
                       } else {
                           self.currentPage = 0;
                       }
                       self.totalPage = Math.ceil(totalRecords / self.recordPerPage);
                   }
               }, {
                   errorMessage : '获取院校信息出错'
               });
           };
       };
    }]
});
