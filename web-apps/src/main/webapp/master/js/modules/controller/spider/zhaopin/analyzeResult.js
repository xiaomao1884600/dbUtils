angular.module('analyzeResult', []).component('analyzeResult', {
    templateUrl : 'app/views/spider/zhaopin/analyzeResult.html',
    controller : ['httpService', '$location', '$anchorScroll', 'ngDialog', '$scope', function analyzeResultController(httpService, $location, $anchorScroll, ngDialog, $scope) {
        var self = this;
        this.retrievingData = false;
        this.sites = [{
            identifier : 'all',
            name : '综合'
        }, {
            identifier : 'zhaopin',
            name : '智联招聘'
        }, {
            identifier : 'chinahr',
            name : '中华英才网'
        }, {
            identifier : 'tongcheng',
            name : '58同城'
        }];

        this.showDetailResult = function (resultType, data) {
            if (resultType === 'jobTypeProvince') {
                self.idColumnLabel = '省份';
                self.detailDataRetrieveAction = 'getJobTypeProvinceAnalyzeResult';
                self.detailParameterValue = data.jobType;
                self.detailTitle = self.selectedSite.name + data.jobType + "分析结果";
            } else if (resultType === "provinceJobType") {
                self.idColumnLabel = "职位";
                self.detailDataRetrieveAction = 'getProvinceJobTypeAnalyzeResult';
                self.detailParameterValue = data.province;
                self.detailTitle = self.selectedSite.name + data.province + "分析结果";
            }

            ngDialog.open({
                template: 'detailDialog',
                className: 'ngdialog-theme-default width-full',
                scope : $scope//此行必须存在，否则无法想弹出组件传值
            });
        };
        this.getJobTypeProvinceAnalyzeResult = function(province) {
            self.showDetailResult('jobTypeProvince', province);
        };
        this.getProvinceJobTypeAnalyzeResult = function(jobType) {
            self.showDetailResult('provinceJobType', jobType);
        };

        this.provinceColumnDefines = [{
            dataId : 'province',
            label : '省份'
        }];
        this.jobTypeColumnDefines = [{
            dataId : 'jobType',
            label : '职位类型'
        }];
        this.generalColumnDefine = [{
            dataId : 'companyCount',
            label : '公司数量'
        }, {
            dataId : 'postCount',
            label : '职位数量'
        }, {
            dataId : 'enrollCount',
            label : '招聘人数'
        }, {
            dataId : 'operations',
            label : '操作'
        }];
        this.provinceColumnDefines = this.provinceColumnDefines.concat(this.generalColumnDefine);

        this.proviceOperations = [{
            text : '详情',
            callback : this.getProvinceJobTypeAnalyzeResult
        }];

        this.jobTypeOperations = [{
            text : '详情',
            callback : this.getJobTypeProvinceAnalyzeResult
        }];

        this.jobTypeColumnDefines = this.jobTypeColumnDefines.concat(this.generalColumnDefine);


        this.getData = function () {
            self.retrievingData = true;
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.doubeye.spider.content.analyzer.service.JobAnalyzeService',
                action : 'getProvinceResult',
                tableIdentifier : self.selectedSite.identifier
            }, {
                dataPropertyName : 'analyzeResult',
                returnRawResponse : true,
                callback : function () {
                    self.retrievingData = false;
                }
            }, {
                errorMessage : '获得招聘汇总信息错误'
            });
        };

        this.onSiteChange = function () {
            self.getData();
        };

        this.toAnchor = function (anchor) {
            $location.hash(anchor);
            $anchorScroll();
        };
    }]
});
