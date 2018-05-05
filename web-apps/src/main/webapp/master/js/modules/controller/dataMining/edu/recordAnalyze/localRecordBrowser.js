/**
 * @see 本功能接口文档https://team.oschina.net/newedu/document
 */
angular.module('localRecordBrowser', []).component('localRecordBrowser', {
    templateUrl: 'app/views/dataMining/edu/recordAnalyze/localRecordBrowser.html',
    controller: ['httpService', '$mdDialog', 'dialogService', '$location', 'authorizationService',
        function localRecordBrowserController(httpService, $mdDialog, dialogService, $location, authorizationService) {

            var self = this;

            this.$onInit = function () {
                this.showDownload = com.doubeye.Utils.compatibility.audioWithoutDownload;
                this.developer = authorizationService.isDeveloper();
                var user = authorizationService.getUser();
                this.loginName = user.loginName;
                this.userId = user.userId;
                this.userName = user.userName;
                this.campusInCharge = authorizationService.getCampusesInCharge();
                this.dataUrl = 'http://record.hxsd.local/api/record/get_lists';
                // todo
                //this.campusTeamUserUrl = 'http://record.hxsd.local/api/record/get_campus_team';
                this.ossPrefix = 'http://hxsd-backup.oss-cn-beijing.aliyuncs.com/';
                this.wordCloudUrl = 'http://record.hxsd.local/api/record/get_word_cloud';
                this.ctiRecordUrl = 'http://record.hxsd.local/api/record/yun_download';
                this.feedbackUrl = 'http://record.hxsd.local/api/edu/get_student_feedback';
                this.totalPage = 0;
                this.recordPerPage = 15;
                this.containTencentResult = false;
                if (this.developer) {
                    this.student = '18581035975';
                }
                this.skipStudentInfo = authorizationService.isSkipStudentInfo();
                this.when_long = '';
                this.whenLongReg = /^\d+|>\d+|>=\d+|<\d+|<=\d+|\d+-\d+$/;
                this.records = [];
                this.campus = [];
                this.teamUsers = [];
                this.teamUsersUnderCampus = [];
                if ($location.search().student) {
                    this.search = $location.search().student;
                }
                if ($location.search().startTime) {
                    this.startTime = $location.search().startTime;
                    this.dateGroup = 'custom';
                } else {
                    this.dateGroup = 'today';
                }
                if ($location.search().endTime) {
                    this.endTime = $location.search().endTime;
                }
                if ($location.search().jump) {
                    this.jump = $location.search().jump;
                }
                this.readFlags = [{
                    id : 0,
                    name : '所有阅读状态'
                }, {
                    id : 1,
                    name : '我没有检查'
                }, {
                    id : 2,
                    name : '无人检查'
                }];

                this.searchRoles = [{
                    id : 0,
                    name : '关键词出现角色'
                }, {
                    id : 1,
                    name : 'ADA'
                }, {
                    id : 2,
                    name : '学生'
                }];
                this.selectedSearchRole = this.searchRoles[0];

                this.feedbackLevels = [{
                    id : 0,
                    name : '所有等级'
                },{
                    id : 1,
                    name : 'A'
                },{
                    id : 2,
                    name : 'B'
                },{
                    id : 3,
                    name : 'C'
                },{
                    id : 4,
                    name : 'D'
                }];
                /* TODO
                this.processUserUnderCampus = function(campus) {
                    var users = [];
                    if (angular.isArray(campus.team_list)) {
                        campus.team_list.forEach(function (team) {
                            if (angular.isArray(team.team_user_list)) {
                                team.team_user_list.forEach(function (user) {
                                    var teamUser = {
                                        userid : user.userid,
                                        username : user.username
                                    };
                                    users.push(teamUser);
                                });
                            }
                        });
                    }
                    return users;
                };
                */
                this.readFlag = this.readFlags[0];
                this.keywordGroups = [];
                this.getKeywordGroups = function () {
                    httpService.sendRequest(self, 'generalRouter', {
                        objectName : 'com.doubeye.datamining.recordanalyze.service.KeywordGroupService',
                        action : 'getAllKeywordGroupNames'
                    }, {
                        dataPropertyName : 'keywordGroups',
                        callback : function (data) {
                            self.keywordGroups.splice(0, 0, {
                                id : 0,
                                name : '所有关键词组'
                            });
                        }
                    }, {
                        errorMessage : '获得关键词组出错，'
                    });
                };
                this.getKeywordGroups();
                //以下部分代码与noInvite代码相同，考虑将其合并 TODO
                /*
                this.processCampusAndTeamUsers = function (data) {
                    self.campus = [];
                    self.teamUsers = [];
                    self.teamUsersUnderCampus = [];
                    data.forEach(function (campus) {
                        self.campus.push({
                            campusId : campus.campusid,
                            name : campus.name,
                            team_list : campus.team_list
                        });
                        var users = self.processUserUnderCampus(campus);
                        self.teamUsers = self.teamUsers.concat(users);
                        self.teamUsersUnderCampus = self.teamUsersUnderCampus.concat(users);
                    });
                    self.campus.splice(0, 0, {
                        campusId: 0,
                        name: '所有校区'
                    });
                    self.teamUsersUnderCampus.splice(0, 0, {
                        userid: 0,
                        username: '所有负责人'
                    });
                    if (self.jump === '1') {
                        if ($location.search().ada) {
                            self.ada = $location.search().ada;
                            self.selectedTeamUser = com.doubeye.Utils.array.getObjectFromArray({userid: self.ada}, self.teamUsers);
                            self.selectedCampus = self.campus[0];
                        }
                        self.getData(1);
                    }
                };
                this.getAuthority = function () {
                    httpService.sendRequest(self, this.campusTeamUserUrl, {
                        campus_id : self.campusInCharge
                    }, {
                        resultPropertyName : 'data',
                        callback : self.processCampusAndTeamUsers
                    }, {
                        errorMessage : '获取用户校区、负责ADA出错，'
                    });
                };

                this.getAuthority();
                */
            };

            this.getWordCloudAndShowText = function (record) {
                httpService.sendRequest(self, self.wordCloudUrl, {
                    record_id : record.record_id
                }, {
                    resultPropertyName : 'data',
                    callback : function (data) {
                        record.ossPath = record.oss_path;
                        record.wordStat = data;
                        self.currentRecord = record;
                        record.is_read = 1;
                        $mdDialog.show({
                            contentElement: '#textBrowser',
                            clickOutsideToClose: true,
                            onRemoving: function (event, removePromise) {
                                var audio = document.getElementById("player");
                                audio.pause();
                            }
                        });
                    }
                }, {
                    errorMessage : '获得录音关键词出错，'
                });
            };

            this.showText = function (record) {
                var audios = document.getElementsByTagName("audio");
                for (var i = 0; i < audios.length; i ++) {
                    audios[i].pause();
                }
                if (record.user_mobile.length === 8) {
                    httpService.sendRequest(record, self.ctiRecordUrl, {
                        FileName : record.oss_path.replace('http://local/', '')
                    }, {
                        resultPropertyName : 'data',
                        callback : function (data) {
                            record.oss_path = data.SignatureUrl;
                            self.getWordCloudAndShowText(record);
                        }
                    });
                } else {
                    self.getWordCloudAndShowText(record);
                }
            };

            /*
            this.onCampusChanged = function () {
                if (self.selectedCampus && self.selectedCampus.campusId > 0) {
                    self.teamUsersUnderCampus = self.processUserUnderCampus(self.selectedCampus);
                } else {
                    self.teamUsersUnderCampus = self.teamUsers;
                }
                self.teamUsersUnderCampus.splice(0, 0, {
                    userid: 0,
                    username: '所有负责人'
                });
                self.selectedTeamUser = null;
            };
            */
            this.processDuration = function (whenLong) {
                //范围
                if (whenLong) {
                    if (whenLong.indexOf('-') > 0) {
                        var startSecondPart = parseInt(whenLong.substr(0, whenLong.indexOf('-')), 10);
                        var endSecondPart = parseInt(whenLong.substr(whenLong.indexOf('-') + 1, whenLong.length), 10);
                        return (startSecondPart) + '-' + (endSecondPart);
                    } else {
                        var secondPart = whenLong.replace(/[^0-9.]/ig,"");
                        return whenLong.replace(secondPart, '') + secondPart;
                    }
                } else {
                    return '';
                }
            };

            this.mergeObjection = function (objections) {
                if (objections) {
                    objections.all = [];
                    if (angular.isArray(objections.student)) {
                        objections.student.forEach(function (objection) {
                            if (angular.isArray(objection.keywords)) {
                                objection.keywords.forEach(function (keyword) {
                                    keyword.studentCount = keyword.count;
                                    keyword.userCount = 0;
                                });
                            }
                            objections.all.push(objection);
                        });
                    }

                    if (angular.isArray(objections.user)) {
                        objections.user.forEach(function (objection) {
                            var entry = com.doubeye.Utils.array.getObjectFromArray({category : objection.category}, objections.all);
                            if (entry) {
                                if (angular.isArray(objection.keywords)) {
                                    objection.keywords.forEach(function (keyword) {
                                        var keywordInStudent = com.doubeye.Utils.array.getObjectFromArray({word : keyword.word}, entry.keywords);
                                        if (keywordInStudent) {
                                            keywordInStudent.userCount = keyword.count;
                                            keywordInStudent.count = keyword.count + keywordInStudent.studentCount;
                                        } else {
                                            entry.keywords.push({
                                                word : keyword.word,
                                                studentCount : 0,
                                                userCount : keyword.count,
                                                count : keyword.count
                                            });
                                        }
                                    });
                                }
                            } else {
                                if (angular.isArray(objection.keywords)) {
                                    objection.keywords.forEach(function (keyword) {
                                        keyword.userCount = keyword.count;
                                        keyword.studentCount = 0;
                                    });
                                    objections.all.push(objection);
                                }
                            }
                        });
                    }
                }
            };

            this.computeObjectionScore = function (record) {
                var score = 0;
                if (record.objection_tags && angular.isArray(record.objection_tags.all)) {
                    record.objection_tags.all.forEach(function (objection) {
                        if (angular.isArray(objection.keywords)) {
                            objection.keywords.forEach(function (keyword) {
                                score += keyword.studentCount * 10 + keyword.userCount * 5
                            });
                        }

                    });
                }
                return score;
            };

            this.getData = function (start, end, page) {
                self.records = [];
                if (!self.recordIds) {
                    //校验通话时长是否满足条件
                    if (self.when_long && !self.whenLongReg.exec(self.when_long)) {
                        $mdDialog.show({
                            template: '<p>输入的时长范围不正确</p>',
                            clickOutsideToClose: true
                        });
                        return;
                    }
                    var campusIds = [];
                    if (self.jump === '1' && $location.search().campusId) {
                        campusIds.push($location.search().campusId)
                    } else {
                        if (!self.selectedCampus || self.selectedCampus.campusId === 0) {
                            self.campus.forEach(function (campus) {
                                if (campus.campusId && campus.campusId !== 0) {
                                    campusIds.push(campus.campusId);
                                }
                            });
                        } else {
                            campusIds.push(self.selectedCampus.campusId);
                        }
                    }
                    var userIds = [];
                    if (self.jump ===  '1' && self.ada) {
                        userIds.push(self.ada);
                    } else {
                        if (!self.selectedTeamUser || self.selectedTeamUser.userid === 0) {
                            self.teamUsersUnderCampus.forEach(function (user) {
                                if (user.userid && user.userid !== 0) {
                                    userIds.push(user.userid);
                                }
                            })
                        } else {
                            userIds.push(self.selectedTeamUser.userid);
                        }
                    }


                    httpService.sendRequest(self, self.dataUrl, {
                        campus_id : campusIds,
                        winner_info : self.userId.indexOf('@') > 0 ? 1 :self.userId,
                        userid : userIds,
                        user_mobile : self.user_mobile,
                        login_user_id : self.userId.indexOf('@') > 0 ? 1 :self.userId,
                        duration : self.processDuration(self.when_long),//将时间转换为秒
                        student : self.search,//学生姓名或手机号
                        start_time : self.startTime,//通话发生时间
                        end_time : self.endTime,//通话发生时间
                        unread : self.readFlag.id,//审查标记
                        keyword : self.keyword,
                        record_lable : self.selectedKeywordGroup ? self.selectedKeywordGroup.id : '',
                        tencent : self.containTencentResult ? 1 : 0,
                        search_role : self.selectedSearchRole ? self.selectedSearchRole.id : 0,
                        feedback_level : self.selectedFeedbackLevel ? self.selectedFeedbackLevel.id : 0,
                        index : page,
                        size : 15
                    }, {
                        dataPropertyName : 'records',
                        resultPropertyName : 'data.list',
                        resultRecordCountPropertyName : 'data.total',
                        callback : function (data, customParams, totalRecords) {
                            data.forEach(function (element) {
                                element.asr_rate = element.asr_rate === 1;
                                element.channel_rate = element.channel_rate === 1;
                                var campus = com.doubeye.Utils.array.getObjectFromArray({campusId : element.record_campus_id}, self.campus);
                                if (campus != null) {
                                    element.campusName = campus.name;
                                }
                                if (element.user_mobile.length === 8) {
                                    element.oss_path = 'http://local/' + element.oss_path.replace(self.ossPrefix, '');
                                }
                                self.mergeObjection(element.objection_tags);
                                element.objectionScore = self.computeObjectionScore(element);
                            });
                            self.totalRecords = totalRecords;
                            if (totalRecords > 0) {
                                self.currentPage = Math.ceil((start + 1) / self.recordPerPage);
                            } else {
                                self.currentPage = 0;
                            }
                            self.totalPage = Math.ceil(totalRecords / self.recordPerPage);
                        }
                    }, {
                        errorMessage : '获得录音识别结果出错，'
                    });
                } else {
                    httpService.sendRequest(self, 'generalRouter', {
                        objectName : 'com.doubeye.record.service.RecordAnalyzeService',
                        action : 'getAnalyzeResultByRecordIds',
                        recordIds : self.recordIds,
                        start : page,
                        size : 15
                    }, {
                        dataPropertyName : 'records',
                        callback : function (data, customParams, totalRecords) {
                            data.forEach(function (element) {
                                element.asr_rate = element.asr_rate === 1;
                                element.channel_rate = element.channel_rate === 1;
                                var campus = com.doubeye.Utils.array.getObjectFromArray({campusid : element.campus_id}, self.campus);
                                if (campus != null) {
                                    element.campusName = campus.name;
                                }
                                if (element.user_mobile.length === 4) {
                                    element.oss_path = self.ossPrefix + element.oss_path;
                                }
                            });
                            if (totalRecords > 0) {
                                self.currentPage = Math.ceil((start + 1) / self.recordPerPage);
                            } else {
                                self.currentPage = 0;
                            }
                            self.totalPage = Math.ceil(totalRecords / self.recordPerPage);
                        }
                    }, {
                        errorMessage : '获得录音识别结果出错，'
                    });
                }
            };

            this.doSearch = function() {
                self.jump = 0;
                self.getData(1);
            };

            this.clearRecordIds = function () {
                self.recordIds = "";
            };

            this.query = function (userName) {
                if (userName) {
                    if (angular.isArray(self.teamUsersUnderCampus)) {
                        var result = [];
                        self.teamUsersUnderCampus.forEach(function (user) {
                            if (user.username.indexOf(userName) >= 0) {
                                result.push(user);
                            }
                        });
                        return result;
                    }
                } else {
                    return self.teamUsersUnderCampus;
                }
            };

            this.onPlay = function () {
                var audios = document.getElementsByTagName("audio");
                for (var i = 0; i < audios.length; i++) {
                    if (audio !== audios[i]) {
                        audios[i].pause();
                    }
                }
                var audio = event.target;
                if (audio.src.indexOf('http://local/') >= 0) {
                    var fileName = audio.src.replace('http://local/', '');

                    return false;
                }
            };


            this.downloadCTIRecord = function (record) {
                httpService.sendRequest(record, self.ctiRecordUrl, {
                    FileName : record.oss_path.replace('http://local/', '')
                }, {
                    resultPropertyName : 'data',
                    callback : function (data) {
                        window.open(data.SignatureUrl);
                    }
                });
            };

            this.showObejctDetail = function (record) {
                self.currentRecord = record;
                $mdDialog.show({
                    contentElement: '#objectionDetial',
                    clickOutsideToClose: true
                });
            } ;


            this.showFeedback = function (record) {
                httpService.sendRequest(record, self.feedbackUrl, {
                    studentid : record.studentid
                }, {
                    resultPropertyName : 'data',
                    callback : function (data) {
                        record.feedback = data;
                        self.currentRecord = record;
                        console.log(record.feedback);
                        $mdDialog.show({
                            contentElement: '#feedback',
                            clickOutsideToClose: true
                        });
                    }
                });
            };

            this.onCampusTeamLoaded = function (campus, teamUsers) {
                self.campus = campus;
                self.teamUsers = teamUsers;
                if (self.jump === '1') {
                    if ($location.search().ada) {
                        self.ada = $location.search().ada;
                        self.selectedTeamUser = com.doubeye.Utils.array.getObjectFromArray({userid: self.ada}, self.teamUsers);
                        self.selectedCampus = self.campus[0];
                    }
                    self.getData(1);
                }
            };
        }]
});
