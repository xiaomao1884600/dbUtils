/**
 * @auhor doubeye
 * @version 1.0.1
 * 分页器
 * history:
 * 1.0.1 :
 *  ! 修正服务端没有返回总记录数时，上一页、下一页、跳转无效的问题
 */

angular.module('dPager', []).component('dPager', {
    templateUrl : 'master/js/components/dPager/dPager.html',
    bindings : {
        /**
         * 当前页
         */
        currentPage : '<',
        /**
         * 总页数
         */
        totalPage : '<',
        /**
         * 总记录数
         */
        totalRecords : '<',
        /**
         * 显示的页数，默认5
         */
        displayPageNumber : '<',
        /**
         * 每页记录数
         */
        recordPerPage : '<',
        /**
         * 分页改变后的回调函数，参数包括
         *  start 开始记录数，
         *  end 结束记录数
         *  page 页数
         */
        callback : '<',
        /**
         * 记录开始于1，默认为true
         */
        recordStartAt1 : '<'
    },
    controller : ['httpService', function dPager(httpService) {
        var self = this;


        this.$onInit = function() {
            if (!self.displayPageNumber) {
                self.displayPageNumber = 5;
            }
            this.previousPages = [];
            this.nextPages = [];

            if (this.recordStartAt1 === undefined || this.recordStartAt1 === null) {
                this.recordStartAt1 = true;
            }

            this.inited = true;
        };
        /**
         * 跳转页数改变事件，用来校验输入的数字是否在正确的页数范围内
         * 如果输入小数，进行四舍五入
         */
        this.onPageChange = function () {
            if (self.toPage < 1) {
                self.toPage = 1;
            } else if (self.totalPage > 0 && self.toPage > self.totalPage) {
                self.toPage = self.totalPage;
            }
            if (Math.floor(self.toPage) != self.toPage) {
                self.toPage = Math.round(self.toPage);
            }
        };

        this.computeStartEndRecord = function (pageNumber) {
            return pageInfo = {
                page : pageNumber,
                start : (pageNumber - 1) * self.recordPerPage + 1,
                end : pageNumber * self.recordPerPage
            };
        };

        this.computePages = function () {
            self.previousPages = [];
            self.nextPages = [];
            var previousSize = Math.floor(self.displayPageNumber / 2);
            for (var i = Math.max((self.currentPage - previousSize), 1); i < self.currentPage; i ++) {
                self.previousPages.push(self.computeStartEndRecord(i));
            }
            var nextStartPage = 0;
            if (self.previousPages.length === 0) {
                nextStartPage = 2;
            } else {
                nextStartPage = self.previousPages[self.previousPages.length -  1].page + 2;
            }
            for (var i = nextStartPage; ((self.previousPages.length + self.nextPages.length + 1) < self.displayPageNumber  && i <= self.totalPage); i ++) {
                self.nextPages.push(self.computeStartEndRecord(i));
            }
            //如果向后翻页，总页数太小，则在前面的页数补页
            if (self.previousPages.length + self.nextPages.length + 1 < self.displayPageNumber) {
                if (self.previousPages.length > 0) {
                    var nowFirstPage = self.previousPages[0].page;
                    //缺的页数
                    var missPageNumber = self.displayPageNumber - (self.previousPages.length + self.nextPages.length + 1);
                    for (var i = 1; i <= missPageNumber; i ++) {
                        if (nowFirstPage > 1) {
                            self.previousPages.splice(0, 0, self.computeStartEndRecord(nowFirstPage - 1));
                            nowFirstPage --;
                        } else {
                            break;
                        }
                    }
                }
            }
        };


        this.goTo = function(page) {
            if (page > 0 && ((page <= self.totalPage) || !self.totalPage)) {
                var pageInfo = self.computeStartEndRecord(page);
                self.computePages();
                if (angular.isFunction(self.callback)) {
                    self.callback(self.recordStartAt1 ? pageInfo.start : pageInfo.start - 1, pageInfo.end, pageInfo.page);
                }
            }

        };

        this.$onChanges = function(changedObject) {
            if ((changedObject.currentPage || changedObject.totalPage) && self.inited) {
                self.computePages();
            }
        }

    }]
});