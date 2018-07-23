package org.gaby.weixin_imsdk;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMGroupMemberRoleType;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMLogListener;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMSoundElem;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.group.TIMGroupBaseInfo;
import com.tencent.imsdk.ext.group.TIMGroupManagerExt;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public abstract class IMUtil {

    private static final String tag = "IMUtil";

    public void init(Application app, int appId, String logPath) {
        TIMSdkConfig config = new TIMSdkConfig(appId).enableCrashReport(false);
        config.enableLogPrint(true).setLogLevel(TIMLogLevel.DEBUG);
        config.setLogPath(logPath).setLogListener(new TIMLogListener() {
            @Override
            public void log(int level, String tag, String msg) {
                IMUtil.this.log(tag, msg);
            }
        });

        //初始化SDK
        TIMManager.getInstance().init(app, config);
    }

    public void enter(final String uid, final String userSig, final String groupId, final ITimListener listener) {
        // 监听消息
        TIMManager.getInstance().addMessageListener(listener);

        TIMManager.getInstance().login(uid, userSig, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code列表请参见错误码表
                log(tag, "login failed. code: " + code + " errmsg: " + desc);
                listener.onError("login", code, "登录聊天服异常:" + desc);
            }

            @Override
            public void onSuccess() {
                log(tag, "login succ");
                queryGroup(groupId, listener);
            }
        });
    }

    protected abstract void log(String tag, String str);

    private void loge(String tag, String str) {
        log(tag, "[error]" + str);
    }

    public void sendMessage(String groupId, String file, int second) {
        TIMConversation conversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, groupId);

        TIMMessage msg = new TIMMessage();
        TIMSoundElem elem = new TIMSoundElem();
        elem.setPath(file);
        elem.setDuration(second);
        //将elem添加到消息
        if (msg.addElement(elem) != 0) {
            log(tag, "addElement failed");
            return;
        }
        //发送消息
        conversation.sendMessage(msg, new TIMValueCallBack<TIMMessage>() {//发送消息回调
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码code和错误描述desc，可用于定位请求失败原因
                //错误码code含义请参见错误码表
                log(tag, "send message failed. code: " + code + " errmsg: " + desc);
            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功
                loge(tag, "SendMsg ok");
            }
        });
    }

    public static abstract class ITimListener implements TIMMessageListener {

        public abstract boolean onNewMessages(List<TIMMessage> var1);

        public abstract void onSuccess();

        public abstract void onError(String step, int code, String desc);
    }

    public void queryGroup(final String groupId, final ITimListener listener) {
        // 第一步，获取原有的群组
        TIMGroupManagerExt.getInstance().getGroupList(new TIMValueCallBack<List<TIMGroupBaseInfo>>() {
            @Override
            public void onError(int code, String desc) {
                log(tag, "error, code=" + code + " desc=" + desc);
                listener.onError("groups", code, "获取已加入的群组失败");
            }

            @Override
            public void onSuccess(List<TIMGroupBaseInfo> timGroupInfos) {
                log(tag, "get group list success");
                List<TIMGroupBaseInfo> quits = new ArrayList<TIMGroupBaseInfo>();

                // 如果有原来的组没退
                if (timGroupInfos != null) {
                    for (TIMGroupBaseInfo info : timGroupInfos) {
                        log(tag, "group id: " + info.getGroupId() + " group name: " + info.getGroupName() + " group type: " + info.getGroupType());
                        if (!groupId.equals(info.getGroupId())) {
                            quits.add(info);
                        }
                    }
                }

                // 退出原有群组
                if (quits.size() > 0) {
                    exitGroups(timGroupInfos);
                }

                // 加入群组
                TIMGroupManager.getInstance().applyJoinGroup(groupId, null, new TIMCallBack() {
                    @Override
                    public void onError(int code, String desc) {
                        loge(tag, "JoinGroup error, code=" + code + " desc=" + desc);
                        if (10015 == code) {
                            createGroup(groupId, listener);
                        } else {
                            createGroup(groupId, listener);
                            listener.onError("join", code, "JoinGroup Error:" + desc);
                        }
                    }

                    @Override
                    public void onSuccess() {
                        Log.i(tag, "JoinGroup success.");
                        listener.onSuccess();
                    }
                });
            }
        });
    }

    /**
     * 群类型, 目前支持的群类型：私有群（Private）、公开群（Public）、
     * 聊天室（ChatRoom）、互动直播聊天室（AVChatRoom）和在线成员广播大群（BChatRoom）
     */
    public void createGroup(String groupId, final ITimListener listener) {
        //创建群组
        TIMGroupManager.CreateGroupParam param = new TIMGroupManager.CreateGroupParam("ChatRoom", "CZ_" + groupId);
        param.setGroupId(groupId);
        param.setIntroduction("Introduction");
        param.setNotification("Notification");
        TIMGroupManager.getInstance().createGroup(param, new TIMValueCallBack<String>() {
            @Override
            public void onError(int code, String desc) {
                log(tag, "create group failed. code: " + code + " errmsg: " + desc);
                listener.onError("create", code, "CreateGroup Error:" + desc);
            }

            @Override
            public void onSuccess(String s) {
                log(tag, "create group succ, groupId:" + s);
                listener.onSuccess();
            }
        });
    }

    public void exitGroups(List<TIMGroupBaseInfo> quits) {
        for (TIMGroupBaseInfo info : quits) {
            exitGroup(info.getGroupId());
        }
    }

    public void exitGroup(String groupId) {
        //退出群组
        TIMGroupManager.getInstance().quitGroup(groupId, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                //错误码code含义请参见错误码表
            }

            @Override
            public void onSuccess() {
                loge(tag, "quit group succ");
            }
        });
    }


}
