package com.northmeter.meshbluecontrol.base;

/**
 * Created by dyd on 2018/12/13.
 */

public class API {
    public static String URL_BASE = "https://devapi.beidiancloud.cn/machine-cloud";//腾讯云服务器
    //public static String URL_BASE = "http://10.168.1.200:8080/machine-cloud";//200服务器
    //public static String URL_BASE = "http://10.168.1.50:801/machine-cloud";//开发者电脑服务器


    /**账户管理--------------------------GET*/
    public static String login = URL_BASE +"/app/customers/login";

    /**项目列表查询--------------------------GET*/
    public static String getProjectList = URL_BASE +"/app/project/list";

    /**项目建筑信息的获取-------------------------GET*/
    public static String getBuildList = URL_BASE +"/app/project/buildinglist";

    /**设备的激活--------------------------GET*/
    public static String doactiveEquipment = URL_BASE +"/app/buildingequipment/active";

    /**单个设备自检--------------------------GET*/
    public static String singleSelfChecking = URL_BASE+"/app/buildingequipment/detection";

    /**单个设备自检结果查询--------------------------GET*/
    public static String singlSeselfCheckingResult = URL_BASE+"/app/buildingequipment/equiptaskResult";

    /**设备的注册--------------------------POST*/
    public static String registereEquipment = URL_BASE +"/app/buildingequipment/regist";

    /**设备列表查询接口--------------------------GET*/
    public static String getEquipList = URL_BASE +"/app/buildingequipment/list";

    /**设备的删除--------------------------POST*/
    public static String deleteEquipment = URL_BASE +"/app/buildingequipment/delete";

    /**新增设备接口--------------------------POST*/
    public static String addBuildingequipment = URL_BASE+"/app/buildingequipment/save";

    /**新增设备时获取配置方案列表--------------------------GET*/
    public static String getConfigurationPlan = URL_BASE+"/app/buildingequipment/getConfigurationPlan";

    /**新增设备时获取产品型号--------------------------GET*/
    public static String getProjetType = URL_BASE+"/app/project/projetType";

    /**设备的详情--------------------------GET*/
    public static String getEquipmentDetails = URL_BASE+"/app/buildingequipment/details";

    /**获取水表最新图片--------------------------GET*/
    public static String getMeterNetWorkPic = URL_BASE+"/app/buildingequipment/getImageUrl";



    /**档案的导入*/
    public static String saveEquipmentRecord = URL_BASE +"/app/equipment/save";

    /**集中器查询接口*/
    public static String getConcentratorList = URL_BASE+"/app/concentrator/list";

    /**档案的下发*/
    public static String saveConcentratorList = URL_BASE+"/app/concentrator/files";



}
