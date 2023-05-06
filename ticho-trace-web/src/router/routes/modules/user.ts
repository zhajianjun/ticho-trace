import type { AppRouteModule } from '/@/router/types';

import { LAYOUT } from '/@/router/constant';

const setup: AppRouteModule = {
  path: '/user',
  name: 'User',
  component: LAYOUT,
  redirect: '/user/index',
  meta: {
    orderNo: 10,
    hideChildrenInMenu: true,
    icon: 'mdi:user-box-multiple',
    title: '用户信息',
  },
  children: [
    {
      path: 'index',
      name: 'UserInfo',
      component: () => import('/@/views/user/index.vue'),
      meta: {
        title: '用户列表',
        icon: 'whh:paintroll',
        hideMenu: true,
      },
    },
  ],
};

export default setup;
