/**
 * @see 本功能接口文档http://record_logs.mydoc.io/
 */
angular.module('companyList', []).component('companyList', {
    templateUrl: 'app/views/spider/zhaopin/company/companyList.html',
    controller: ['httpService', '$mdDialog',
        function companyListController(httpService, $mdDialog) {

            var self = this;
            this.companyName = '广州市银河在线饰品有限公司';
            this.serviceObjectName = "com.doubeye.spider.job.service.CompanyService";
            this.actions = {
                getCompanies : "getCompanies"
            };
            this.recordPerPage = 10;
            this.$onInit = function () {
                this.objectName = '';
                this.companies = [];
                this.__columnDefines = [{
                    dataId : 'company_name',
                    label : '公司名称'
                }, {
                    dataId : 'organization_code',
                    label : '组织机构代码'
                }, {
                    dataId : 'official_site',
                    label : '公司官网',
                    html : '<a target="_blank"></a>'
                }];
                this.__operations = [{
                    text : '详情',
                    callback : function(record) {
                        self.showText(record);
                    }
                }];
            };

            this.showText = function (record) {
                self.currentCompany = record;
                $mdDialog.show({
                    contentElement: '#companyDetail',
                    clickOutsideToClose: true
                });
            };


            this.getData = function (start, end, page) {
                httpService.sendRequest(self, com.doubeye.Utils.application.generalRouter, {
                    objectName : self.serviceObjectName,
                    action : self.actions.getCompanies,
                    companyName : self.companyName,
                    start : start,
                    size : 10
                }, {
                    dataPropertyName : 'companies',
                    callback : function (data, customParams, totalRecords) {
                        self.currentPage = Math.ceil((start + 1) / self.recordPerPage);
                    }
                }, {
                    errorMessage : '获取公司列表出错，'
                });
            };
        }]
});
