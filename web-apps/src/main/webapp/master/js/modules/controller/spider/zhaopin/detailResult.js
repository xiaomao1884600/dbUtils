angular.module('detailResult', []).component('detailResult', {
    templateUrl : 'app/views/spider/zhaopin/detailResult.html',
    bindings : {
        type : '<',
        parameterValue : '<',
        detailRetrieveAction : '<',
        idColumnLabel : '<',
        tableIdentifier : '<',
        title : '<'
    },
    controller : ['httpService', '$location', '$anchorScroll', 'ngDialog', '$scope', function detailResultController(httpService, $location, $anchorScroll, ngDialog, $scope) {
        var self = this;
        this.analyzeResult = {};
        this.provinceJobTypeResult = {};



        this.hideDetail = true;

        this.processProvinceJobTypeResult = function(data) {
            self.processPieData(data, "provinceJobTypeResult", "experienceData", "experienceLegends", "experience");
            self.processPieData(data, "provinceJobTypeResult", "degreeData", "degreeLegends", "degree");
            self.processPieData(data, "provinceJobTypeResult", "salaryData", "salaryLegends", "salary");
            self.hideDetail = false;
        };

        this.getDetailAnalyzeResult = function (value) {
            var province =  self.idColumnLabel === '省份' ? value.id : self.parameterValue;
            var jobType = self.idColumnLabel === '职位' ? value.id : self.parameterValue;
            self.provincePostTitle = province + jobType + "分析结果";
                httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.doubeye.spider.content.analyzer.service.JobAnalyzeService',
                action : 'getProvinceAndJobTypeAnalyzeResult',
                province : province,
                jobType : jobType,
                tableIdentifier : self.tableIdentifier
            }, {
                dataPropertyName : 'provinceJobTypeResult',
                returnRawResponse : true,
                callback : self.processProvinceJobTypeResult
            }, {
                errorMessage : '获得招聘详细分析结果错误'
            });
        };

        this.columnDefines = [{
            dataId : 'id',
            label : this.idColumnLabel
        }, {
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

        this.operations = [{
            text : '详情',
            callback : this.getDetailAnalyzeResult
        }];


        this.processPieData = function(data, valueProperty, dataPropertyName, legendPropertyName, idPropertyName) {
            var chartData = data[idPropertyName];
            self[valueProperty][dataPropertyName] = [];
            self[valueProperty][legendPropertyName] = [];
            chartData.forEach(function (element) {
                self[valueProperty][dataPropertyName].push({
                    name : element[idPropertyName],
                    value : element.cnt
                });
                self[valueProperty][legendPropertyName].push(element[idPropertyName]);
            });
        };

        this.processResult = function (data) {
            self.processPieData(data, "analyzeResult", "experienceData", "experienceLegends", "experience");
            self.processPieData(data, "analyzeResult", "degreeData", "degreeLegends", "degree");
            self.processPieData(data, "analyzeResult", "salaryData", "salaryLegends", "salary");
        };

        httpService.sendRequest(self, 'generalRouter', {
            objectName : 'com.doubeye.spider.content.analyzer.service.JobAnalyzeService',
            action : self.detailRetrieveAction,
            id : self.parameterValue,
            tableIdentifier : self.tableIdentifier
        }, {
            dataPropertyName : 'analyzeResult',
            returnRawResponse : true,
            callback : self.processResult
        }, {
            errorMessage : '获得招聘详细分析结果错误'
        });
        this.onDetailBlur = function () {
            this.hideDetail = true;
        }
    }]
});
