import { defHttp } from '/@/utils/http/axios';
import { Log } from '/@/api/log/model/logModel';

enum Api {
  LogPage = '/log/page',
}

export function logPage(params: any) {
  return defHttp.get<Log>({ url: Api.LogPage, params }, { errorMessageMode: 'none' });
}
