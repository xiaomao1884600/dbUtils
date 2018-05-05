/**
 * Created by zhanglu1782 on 2017/04/23.
 */
angular.module('compareJson', []).component('compareJson', {
    templateUrl : 'app/views/compareJSON.html',
    controller : ['httpService', function compareJsonController(httpService) {
        var self = this;
        self.firstJson = JSON.stringify({
            "success":true,
            "errorMessage":"",
            "data":{
                "workstatus":1,
                "studentgradecount":{
                    "noenrolled":{
                        "enrolllevel":{
                            "B":61327,
                            "D":1971,
                            "A":2455
                        }
                    },
                    "enrolled":46628,
                    "recover":1954
                },
                "unassignedstudent":{
                    "allcampus":[
                        {a : "3"}
                    ],
                    "total":0
                },
                "remindfeedback":0
            }
        });
        self.secondJson = JSON.stringify({
            "success":true,
            "errorMessage":"",
            "errorCode":200,
            "data":{
                "workstatus":0,
                "studentgradecount":{
                    "noenrolled":{
                        "total":69716,
                        "enrolllevel":{
                            "B":61327,
                            "C":3963,
                            "D":1971,
                            "A":2455
                        }
                    },
                    "enrolled":46628,
                    "recover":1954
                },
                "unassignedstudent":{
                    "allcampus":[

                    ],
                    "total":0
                },
                "remindfeedback":0
            }
        });
        this.firstTree = [];
        this.secondTree = [];
        this.resultTree = [];
        this.firstTreeComponent = {};
        this.secondTreeComponent = {};
        this.resultTreeComponent = {};

        this.doCompare = function () {
            this.firstTree = [];
            this.secondTree = [];
            com.doubeye.Utils.collection.toAbnTreeJSON(JSON.parse(self.firstJson), self.firstTree);
            com.doubeye.Utils.collection.toAbnTreeJSON(JSON.parse(self.secondJson), self.secondTree);
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.hxsd.services.utils.JSONCompareService',
                action : 'compareJSON',
                firstJSON : self.firstJson,
                secondJSON : self.secondJson
            }, {
                callback : self.processCompareResult
            }, {
                errorMessage : '比较JSON出错，'
            });
            this.firstTreeComponent.expand_all();
            this.secondTreeComponent.expand_all();
        };
        this.expandResult = function () {
            this.firstTreeComponent.expand_all();
            this.secondTreeComponent.expand_all();
            this.resultTreeComponent.expand_all();
        };

        this.collapseResult = function () {
            this.firstTreeComponent.collapse_all();
            this.secondTreeComponent.collapse_all();
            this.resultTreeComponent.collapse_all();
        };
        self.processCompareResult = function(result) {
            self.resultTree = [];
            com.doubeye.Utils.collection.toAbnTreeJSON(result, self.resultTree);
            com.doubeye.Utils.collection.abnTreeJSONCompareResultWrapper(self.firstTree, result, true);
            com.doubeye.Utils.collection.abnTreeJSONCompareResultWrapper(self.secondTree, result, false);
            self.result = JSON.stringify(result);
            self.resultTreeComponent.expand_all();
        };
    }]
});