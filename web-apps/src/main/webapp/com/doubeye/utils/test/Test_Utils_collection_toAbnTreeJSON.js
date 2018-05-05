jQuery(document).ready(function() {
    test_toAbnTreeJSON();
});

test_toAbnTreeJSON = function() {
    jQuery('<input>', {
        value : 'test_toAbnTreeJSON',
        type : 'button'
    }).click(function() {
        var json = {"success":true,"errorMessage":"","errorCode":200,"data":{"workstatus":0,"studentgradecount":{"noenrolled":{"total":69716,"enrolllevel":{"B":61327,"C":3963,"D":1971,"A":2455}},"enrolled":46628,"recover":1954},"unassignedstudent":{"allcampus":[],"total":0},"remindfeedback":0}};
        /*
        json = {
            "data_reportstudent_unterm":1,
            "btn_adjustmentdetail":1,
            "data_report_switchreport":1
        };
         */
        var result = [];
        com.doubeye.Utils.collection.toAbnTreeJSON(json, result);
        new com.doubeye.BubbleDialog({
            title : 'test',
            message : JSON.stringify(result)
        }).init().render();
    }).appendTo('#container').trigger('click');
};