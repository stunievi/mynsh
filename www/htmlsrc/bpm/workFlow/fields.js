;(function () {
    if (typeof $ == 'undefined') {
        return
    }
    $.fn.extend({
        fsSelector: function (prefix, fs, values) {
            var t = this;
            $.get("./fields_template.html", function (html) {
                var tt = $(html);
                t.html(tt.siblings(".selection-container"));

                var tmpl = template.compile(tt.siblings(".field_item").html())
                t.find(".form-fields").html(tmpl({
                    prefix: prefix,
                    fields: fs
                }))


                //bind
                t.find('.checkbox-all').click(function () {
                    if ($(this).prop('checked')) {
                        $(this).parent().next().find('.checkboxs').prop('checked', true);
                    } else {
                        $(this).parent().next().find('.checkboxs').prop('checked', false);
                    }
                    btn_status();
                })
                // t.find('.select-content').on('click', '.checkboxs', function (e) {
                //
                //     // return false;
                // });
                t.find('.select-content').on('click', 'li', function (e) {
                    // $(this).children('.checkboxs').click();
                    var chk = $(this).find("input[type=checkbox]")[0];
                    chk.checked = !chk.checked;

                    var checkedAll = $(this).closest('.select-content').prev().find('.checkbox-all');
                    var checkboxs = $(this).prop('checked');
                    if (!checkboxs && checkedAll.prop('checked')) {
                        checkedAll.prop('checked', false);
                    } else if (checkboxs && !checkedAll.prop('checked')) {
                        var lis = $(this).closest('ul').children();
                        for (var i = 0; i < lis.length; i++) {
                            if ($(lis[i]).find('.checkboxs').prop('checked')) {
                                if (i == lis.length - 1) {
                                    checkedAll.prop('checked', true)
                                }
                            } else {
                                break;
                            }
                        }
                    }

                    btn_status();
                    return false;
                    // stopFunc(e);
                })

                function btn_status() {
                    var btn1 = t.find('.right')[0]
                    var btn2 = t.find('.left')[1]
                    var left_ul = t.find('.unselect-ul')
                    var right_ul = t.find('.selected-ul')
                    var left_ul_li = left_ul[0].children
                    var right_ul_li = right_ul[0].children
                    var left_btn = false
                    var right_btn = false
                    for (var i = 0; i < left_ul_li.length; i++) {
                        if (left_ul_li[i].firstElementChild.checked == true) {
                            left_btn = true
                        }
                    }
                    for (var i = 0; i < right_ul_li.length; i++) {
                        if (right_ul_li[i].firstElementChild.checked == true) {
                            right_btn = true
                        }
                    }
                    if (left_btn) {
                        btn1.classList.add('btn-cursor')
                    } else {
                        btn1.classList.remove('btn-cursor')
                    }
                    if (right_btn) {
                        btn2.classList.add('btn-cursor')
                    } else {
                        btn2.classList.remove('btn-cursor')
                    }

                }

                t.find('.arrow-btn').click(function () {
                    var checkboxs, origin, target, num = 0;
                    if ($(this).hasClass('right')) {
                        origin = t.find('.unselect-ul');
                        target = t.find('.selected-ul');
                    } else {
                        origin = t.find('.selected-ul');
                        target = t.find('.unselect-ul');
                    }
                    checkboxs = origin.find('.checkboxs');
                    for (var i = 0; i < checkboxs.length; i++) {
                        if ($(checkboxs[i]).prop('checked')) {
                            var that = $(checkboxs[i]).parent().clone();
                            that.children('input').prop('checked', false);
                            target.append(that);
                            $(checkboxs[i]).parent().remove();
                        } else {
                            num++;
                        }
                    }
                    if (checkboxs.length == num) {
                    } else {
                        origin.parent().prev().find('.checkbox-all').prop('checked', false);
                    }
                    btn_status();

                    updateData();
                })

                function updateData() {
                    // var arr = [];
                    values.length = 0;
                    t.find(".unselect-ul li").each(function () {
                        var name = $(this).find("span").text();
                        // arr.push(name);
                        values.push(name)
                    });
                    // arr
                    // opener.bpmData.nodes[nodeId].fields.all_fields = arr;
                }


                if (values) {
                    $.each(values, function (i, v) {
                        t.find(".form-fields input[id='" + prefix + "-tyue-checkbox-blue-" + v + "']").closest("li").click();
                    })
                    t.find(".arrow-btn.left").click()
                }

                // function stopFunc(e) {
                //     e.stopPropagation ? e.stopPropagation() : e.cancelBubble = true;
                // }
            });

            return this;
        }
    })
})();