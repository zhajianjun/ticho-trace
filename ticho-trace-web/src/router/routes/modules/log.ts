import type { AppRouteModule } from '/@/router/types';

import { LAYOUT } from '/@/router/constant';

const setup: AppRouteModule = {
  path: '/log',
  name: 'Log',
  component: LAYOUT,
  redirect: '/log/index',
  meta: {
    orderNo: 30,
    hideChildrenInMenu: true,
    icon: 'octicon:log-24',
    title: '日志信息',
  },
  children: [
    {
      path: 'index',
      name: 'LogPage',
      component: () => import('/@/views/log/index.vue'),
      meta: {
        title: '日志列表',
        icon: 'whh:paintroll',
        hideMenu: true,
      },
    },
  ],
};

export default setup;
