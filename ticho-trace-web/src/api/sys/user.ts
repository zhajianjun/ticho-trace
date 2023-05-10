import {defHttp} from '/@/utils/http/axios';
import {LoginParams, LoginResultModel} from './model/userModel';

import {ErrorMessageMode} from '/#/axios';
import {UserInfo} from '/#/store';
import {ContentTypeEnum} from "/@/enums/httpEnum";

enum Api {
  Login = '/oauth/token',
  Logout = '/logout',
  GetUserInfo = '/user/getByUsername',
  GetPermCode = '/getPermCode',
  TestRetry = '/testRetry',
}

/**
 * @description: user login api
 */
export function loginApi(params: LoginParams, mode: ErrorMessageMode = 'modal') {
  return defHttp.post<LoginResultModel>(
    {
      url: Api.Login,
      headers: {'Content-Type': ContentTypeEnum.FORM_URLENCODED},
      params,
    },
    {
      errorMessageMode: mode,
    },
  );
}

/**
 * @description: getUserInfo
 */
export function getUserInfo() {
  return defHttp.get<UserInfo>({url: Api.GetUserInfo}, {errorMessageMode: 'none'});
}

export function getPermCode() {
  return defHttp.get<string[]>({url: Api.GetPermCode});
}

export function doLogout() {
  return defHttp.get({url: Api.Logout});
}

export function testRetry() {
  return defHttp.get(
    {url: Api.TestRetry},
    {
      retryRequest: {
        isOpenRetry: true,
        count: 5,
        waitTime: 1000,
      },
    },
  );
}
