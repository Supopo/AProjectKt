# AProjectKt
mvvm kotlin 版本, 方便新项目快速上手.
1.修改build.gradle中的namespace之后，activity和fragment中的资源文件要重新导包。
2.Fragment跳转动作需要在navigation中配置。

QMUI部分指南：
TipDialog

    QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                .setTipWord("正在加载")
                                .create();
                                tipDialog.show();
    //ICON_TYPE_SUCCESS成功 ICON_TYPE_FAIL失败 ICON_TYPE_INFO提示

    //自定义
    QMUITipDialog tipDialog = new QMUITipDialog.CustomBuilder(getContext())
                                .setContent(R.layout.tipdialog_custom)
                                .create();

    //单独文字
    QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                                .setTipWord("请勿重复操作")
                                .create();
    //单独图标
    QMUITipDialog tipDialog = new QMUITipDialog.Builder(getContext())
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                                .create();

Dialog部分

    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    //蓝色按钮
    private void showMessagePositiveDialog() {
        new QMUIDialog.MessageDialogBuilder(mcContext)
                .setTitle("标题")
                .setMessage("确定要发送吗？")
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction("确定", (dialog, index) -> {
                    dialog.dismiss();
                    Toast.makeText(mcContext, "发送成功", Toast.LENGTH_SHORT).show();
                })
                .create(mCurrentDialogStyle) //可去掉
                .show();
    }

    //取消按钮蓝色，删除按钮红色
    private void showMessageNegativeDialog() {
        new QMUIDialog.MessageDialogBuilder(mcContext)
                .setTitle("标题")
                .setMessage("确定要删除吗？")
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction(0, "删除", QMUIDialogAction.ACTION_PROP_NEGATIVE, (dialog, index) -> {
                    Toast.makeText(mcContext, "删除成功", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .create(mCurrentDialogStyle).show();
    }

    //可滑动内容
    private void showLongMessageDialog() {
            new QMUIDialog.MessageDialogBuilder(mcContext)
                    .setTitle("标题")
                    .setMessage("这是一段很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                            "很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很" +
                            "很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很" +
                            "很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很" +
                            "长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                            "很长很长很长很长很很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                            "很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                            "很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                            "很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                            "很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长" +
                            "很长很长很长很长很长很长很长很长很长很长很长很长很长很长长很长的文案")
                    .addAction("取消", (dialog, index) -> dialog.dismiss())
                    .create(mCurrentDialogStyle).show();
    }

//带CheckBox类型
private void showConfirmMessageDialog() {
new QMUIDialog.CheckBoxMessageDialogBuilder(mcContext)
.setTitle("退出后是否删除账号信息?")
.setMessage("删除账号信息")
.setChecked(true)
.addAction("取消", (dialog, index) -> dialog.dismiss())
.addAction("退出", (dialog, index) -> dialog.dismiss())
.create(mCurrentDialogStyle).show();
}

    //单选菜单类型
    private void showMenuDialog() {
        final String[] items = new String[]{"选项1", "选项2", "选项3"};
        new QMUIDialog.MenuDialogBuilder(mcContext)
                .addItems(items, (dialog, which) -> {
                    Toast.makeText(mcContext, "你选择了 " + items[which], Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .create(mCurrentDialogStyle).show();
    }

    //单选菜单类型，选中有对号
    private void showMenuDialog() {
        final String[] items = new String[]{"选项1", "选项2", "选项3"};
        new QMUIDialog.MenuDialogBuilder(mcContext)
                .addItems(items, (dialog, which) -> {
                    Toast.makeText(mcContext, "你选择了 " + items[which], Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .create(mCurrentDialogStyle).show();
    }

    //多选类型
    private void showMultiChoiceDialog() {
        final String[] items = new String[]{"选项1", "选项2", "选项3", "选项4", "选项5", "选项6"};
        final QMUIDialog.MultiCheckableDialogBuilder builder = new QMUIDialog.MultiCheckableDialogBuilder(mcContext)
                .setCheckedItems(new int[]{1, 3})
                .addItems(items, (dialog, which) -> {
                });
        builder.addAction("取消", (dialog, index) -> dialog.dismiss());
        builder.addAction("提交", (dialog, index) -> {
            StringBuilder data = new StringBuilder("你选择了 ");
            for (int i = 0; i < builder.getCheckedItemIndexes().length; i++) {
                data.append("").append(builder.getCheckedItemIndexes()[i]).append("; ");
            }
            Toast.makeText(mcContext, data.toString(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        builder.create(mCurrentDialogStyle).show();
    }

    //多选可滑动类型
    private void showNumerousMultiChoiceDialog() {
            final String[] items = new String[]{
                    "选项1", "选项2", "选项3", "选项4", "选项5", "选项6",
                    "选项7", "选项8", "选项9", "选项10", "选项11", "选项12",
                    "选项13", "选项14", "选项15", "选项16", "选项17", "选项18"
            };
            final QMUIDialog.MultiCheckableDialogBuilder builder = new QMUIDialog.MultiCheckableDialogBuilder(mcContext)
                    .setCheckedItems(new int[]{1, 3})
                    .addItems(items, (dialog, which) -> {
                    });
            builder.addAction("取消", (dialog, index) -> dialog.dismiss());
            builder.addAction("提交", (dialog, index) -> {
                StringBuilder data = new StringBuilder("你选择了 ");
                for (int i = 0; i < builder.getCheckedItemIndexes().length; i++) {
                    data.append("").append(builder.getCheckedItemIndexes()[i]).append("; ");
                }
                Toast.makeText(mcContext, data.toString(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
            builder.create(mCurrentDialogStyle).show();
    }

    //带输入框类型
    private void showEditTextDialog() {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(mcContext);
        builder.setTitle("标题")
                .setPlaceholder("在此输入您的昵称")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction("确定", (dialog, index) -> {
                    CharSequence text = builder.getEditText().getText();
                    if (text != null && text.length() > 0) {
                        Toast.makeText(mcContext, "您的昵称: " + text, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(mcContext, "请填入昵称", Toast.LENGTH_SHORT).show();
                    }
                })
                .create(mCurrentDialogStyle).show();
    }

    //高度自适应对话框类型
    private void showAutoDialog() {
        QMAutoTestDialogBuilder autoTestDialogBuilder = (QMAutoTestDialogBuilder) new QMAutoTestDialogBuilder(mcContext)
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction("确定", (dialog, index) -> {
                    Toast.makeText(mcContext, "你点了确定", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
        autoTestDialogBuilder.create(mCurrentDialogStyle).show();
        QMUIKeyboardHelper.showKeyboard(autoTestDialogBuilder.getEditText(), true);
    }

    class QMAutoTestDialogBuilder extends QMUIDialog.AutoResizeDialogBuilder {
            private Context mContext;
            private EditText mEditText;

            public QMAutoTestDialogBuilder(Context context) {
                super(context);
                mContext = context;
            }

            public EditText getEditText() {
                return mEditText;
            }

            @Override
            public View onBuildContent(QMUIDialog dialog, ScrollView parent) {
                LinearLayout layout = new LinearLayout(mContext);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setLayoutParams(new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                int padding = QMUIDisplayHelper.dp2px(mContext, 20);
                layout.setPadding(padding, padding, padding, padding);
                mEditText = new EditText(mContext);
                QMUIViewHelper.setBackgroundKeepingPadding(mEditText, QMUIResHelper.getAttrDrawable(mContext, R.attr.qmui_list_item_bg_with_border_bottom));
                mEditText.setHint("输入框");
                LinearLayout.LayoutParams editTextLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, QMUIDisplayHelper.dpToPx(50));
                editTextLP.bottomMargin = QMUIDisplayHelper.dp2px(mcContext, 15);
                mEditText.setLayoutParams(editTextLP);
                layout.addView(mEditText);
                TextView textView = new TextView(mContext);
                textView.setLineSpacing(QMUIDisplayHelper.dp2px(mcContext, 4), 1.0f);
                textView.setText("观察聚焦输入框后，键盘升起降下时 dialog 的高度自适应变化。\n\n" +
                        "QMUI Android 的设计目的是用于辅助快速搭建一个具备基本设计还原效果的 Android 项目，" +
                        "同时利用自身提供的丰富控件及兼容处理，让开发者能专注于业务需求而无需耗费精力在基础代码的设计上。" +
                        "不管是新项目的创建，或是已有项目的维护，均可使开发效率和项目质量得到大幅度提升。");
                textView.setTextColor(ContextCompat.getColor(mcContext, R.color.app_color_description));
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                layout.addView(textView);
                return layout;
            }
        }


底部弹窗

        //list类型
        private void showSimpleBottomSheetList() {
            new QMUIBottomSheet.BottomListSheetBuilder(mContext)
                    .addItem("Item 1")
                    .addItem("Item 2")
                    .addItem("Item 3")
                    .setOnSheetItemClickListener((dialog, itemView, position, tag) -> {
                        dialog.dismiss();
                        Toast.makeText(mContext, "Item " + (position + 1), Toast.LENGTH_SHORT).show();
                    })
                    .build()
                    .show();
        }


        //Grid类型
        private void showSimpleBottomSheetGrid() {
            final int TAG_SHARE_WECHAT_FRIEND = 0;
            final int TAG_SHARE_WECHAT_MOMENT = 1;
            final int TAG_SHARE_WEIBO = 2;
            final int TAG_SHARE_CHAT = 3;
            final int TAG_SHARE_LOCAL = 4;
            new QMUIBottomSheet.BottomGridSheetBuilder(mContext)
                    .addItem(R.mipmap.ic_launcher, "分享到微信", TAG_SHARE_WECHAT_FRIEND, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                    .addItem(R.mipmap.ic_launcher, "分享到朋友圈", TAG_SHARE_WECHAT_MOMENT, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                    .addItem(R.mipmap.ic_launcher, "分享到微博", TAG_SHARE_WEIBO, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                    .addItem(R.mipmap.ic_launcher, "分享到私信", TAG_SHARE_CHAT, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                    .addItem(R.mipmap.ic_launcher, "保存到本地", TAG_SHARE_LOCAL, QMUIBottomSheet.BottomGridSheetBuilder.SECOND_LINE)
                    .setOnSheetItemClickListener((dialog, itemView) -> {
                        dialog.dismiss();
                        int tag = (int) itemView.getTag();
                        switch (tag) {
                            case TAG_SHARE_WECHAT_FRIEND:
                                Toast.makeText(mContext, "分享到微信", Toast.LENGTH_SHORT).show();
                                break;
                            case TAG_SHARE_WECHAT_MOMENT:
                                Toast.makeText(mContext, "分享到朋友圈", Toast.LENGTH_SHORT).show();
                                break;
                            case TAG_SHARE_WEIBO:
                                Toast.makeText(mContext, "分享到微博", Toast.LENGTH_SHORT).show();
                                break;
                            case TAG_SHARE_CHAT:
                                Toast.makeText(mContext, "分享到私信", Toast.LENGTH_SHORT).show();
                                break;
                            case TAG_SHARE_LOCAL:
                                Toast.makeText(mContext, "保存到本地", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    })
                    .build().show();
        }


PopupWindow

    //普通类型
    private void showNormalPopup(View v, int preferredDirection) {
        QMUIPopup mNormalPopup = new QMUIPopup(mContext, QMUIPopup.DIRECTION_NONE);
        TextView textView = new TextView(mContext);
        textView.setLayoutParams(mNormalPopup.generateLayoutParam(QMUIDisplayHelper.dp2px(mContext, 250),
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setLineSpacing(QMUIDisplayHelper.dp2px(mContext, 4), 1.0f);
        int padding = QMUIDisplayHelper.dp2px(mContext, 20);
        textView.setPadding(padding, padding, padding, padding);
        textView.setText("Popup 可以设置其位置以及显示和隐藏的动画");
        textView.setTextColor(ContextCompat.getColor(mContext, R.color.app_color_description));
        mNormalPopup.setContentView(textView);
        mNormalPopup.setOnDismissListener(() -> Toast.makeText(mContext, "onDismiss", Toast.LENGTH_SHORT).show());

        mNormalPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
        mNormalPopup.setPreferredDirection(preferredDirection);//QMUIPopup.DIRECTION_TOP、DIRECTION_BOTTOM、DIRECTION_NONE
        mNormalPopup.show(v);
    }


    //列表类型
    private void showListPopup(View v, int preferredDirection) {
        String[] array = new String[]{
                "Item 1",
                "Item 2",
                "Item 3",
                "Item 4",
                "Item 5",
        };
        ArrayAdapter adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, new ArrayList<>(Arrays.asList(array)));
        QMUIListPopup mListPopup = new QMUIListPopup(mContext, QMUIPopup.DIRECTION_NONE, adapter);
        mListPopup.create(QMUIDisplayHelper.dp2px(mContext, 250), QMUIDisplayHelper.dp2px(mContext, 200),
                (adapterView, view, i, l) -> {
                    Toast.makeText(mContext, "Item " + (i + 1), Toast.LENGTH_SHORT).show();
                    mListPopup.dismiss();
                });
        mListPopup.setOnDismissListener(() -> Toast.makeText(mContext, "onDismiss", Toast.LENGTH_SHORT).show());

        mListPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
        mListPopup.setPreferredDirection(preferredDirection);
        mListPopup.show(v);
         }
    }


    //获取相册权限用法
    Disposable disposable = permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                            .subscribe(granted -> {
                                if (granted) {
                                    selectImage();
                                } else {
                                    ToastUtils.showShort("访问权限已拒绝");
                                }

                            });

