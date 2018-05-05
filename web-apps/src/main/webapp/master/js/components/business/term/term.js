/**
 * Created by doubeye
 * 期数组件
 */
angular.module('term', []).component('term', {
    templateUrl: 'master/js/components/business/term/term.html',
    bindings : {
        /**
         * 默认期数，如果是数字则表示选中指定期数，如果为'current'，则表示选中当前期, 如果表示
         */
        selectedTermId : '<',
        /**
         * 下拉列表中的开始期数
         */
        startTermId : '<',
        /**
         * 下拉列表中的结束期数
         */
        endTermId : '<',
        /**
         * 选中的期数对象
         */
        selectedTerm : '=',
        /**
         * 选择改变事件，将选中期数作为参数返回
         */
        onChange : '<',
        /**
         * 数据加载后的回调参数，将所有的期数作为参数返回
         */
        onLoaded : '<'
    },
    controller: ['httpService',
        function termController(httpService) {
            var self = this;
            this.$onInit = function () {
                this.termUrl = 'http://record.hxsd.local/api/record/get_term';
                this.terms = [];
                this.termComparator = function (term) {
                    if (self.selectedTermId === 'current') {
                        return term.current;
                    } else if (self.selectedTermId === 'next'){
                        return term.next;
                    } else {
                        return term.termid === self.selectedTermId;
                    }
                };
                this.getTerms = function () {
                    httpService.sendRequest(self, self.termUrl, {
                        start_term_id : self.startTermId,
                        end_term_id : self.endTermId
                    }, {
                        dataPropertyName : 'terms',
                        resultPropertyName : 'data',
                        callback : function (data) {
                            if (angular.isArray(self.terms)) {
                                if (angular.isFunction(self.onLoaded)) {
                                    self.onLoaded(self.terms);
                                }
                                for (var i = 0; i < self.terms.length; i ++) {
                                    if (self.termComparator(self.terms[i])) {
                                        self.selectedTerm = self.terms[i];
                                        self.onSelectChange();
                                        break;
                                    }
                                }
                            }
                        }
                    });
                };
                this.onSelectChange = function () {
                    if (angular.isFunction(self.onChange)) {
                        self.onChange(self.selectedTerm);
                    }
                };
                this.getTerms();
            }//END OF $onInit
        }]//EDN OF CONTROLLER
});
