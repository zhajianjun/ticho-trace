import { defHttp } from '/@/utils/http/axios';
import { LoginRequest, Oauth2AccessToken } from '/@/api/sys/model/loginModel';
import { User, UserQuery } from '/@/api/sys/model/userModel';

import { ErrorMessageMode } from '/#/axios';
import { ContentTypeEnum } from '/@/enums/httpEnum';

enum Api {
  Login = '/oauth/token',
  GetUserInfo = '/user/getByUsername',
  UserPage = '/user/page',
  GetPermCode = '/getPermCode',
}

/**
 * @description: user login api
 */
export function loginApi(params: LoginRequest, mode: ErrorMessageMode = 'modal') {
  return defHttp.post<Oauth2AccessToken>(
    {
      url: Api.Login,
      headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED },
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
  return defHttp.get<User>({ url: Api.GetUserInfo }, { errorMessageMode: 'none' });
}

export function userPage(params: UserQuery) {
  return defHttp.get<User>({ url: Api.UserPage, params }, { errorMessageMode: 'none' });
}

export function getPermCode() {
  return defHttp.get<string[]>({ url: Api.GetPermCode });
}
