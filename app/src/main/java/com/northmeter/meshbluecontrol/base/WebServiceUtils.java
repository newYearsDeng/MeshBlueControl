package com.northmeter.meshbluecontrol.base;


/**
 * Created by dyd on 2017/8/24.
 */
public class WebServiceUtils {
    public interface CallBack {
        String resultSuccess(String result);
        String resultFail(String result);
    }
//
//    public static void getServiceInfo(final Context context , Map jsonString, final CallBack callBack) {
//        Gson gson = new Gson();
//        OkGo.<CommonResponse>post(API.sendMiddleware)
//                .tag(context)
//                .isSpliceUrl(true)//post请求的url上拼接上参数
//                .headers("token", SaveUserInfo.getLoginUser(context).getToken())
//                .upJson(gson.toJson(jsonString))
//                .execute(new DialogCallback<CommonResponse>((Activity) context,CommonResponse.class) {
//                             @Override
//                             public void onSuccess(Response<CommonResponse> response) {
//                                 callBack.resultSuccess(response.body());
//                             }
//
//                             @Override
//                             public void onError(Response<CommonResponse> response) {
//                                 super.onError(response);
//                                 callBack.resultFail("Error");
//                             }
//                         }
//                );
//    }


}
