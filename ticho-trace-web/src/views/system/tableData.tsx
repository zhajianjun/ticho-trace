import { FormProps } from '/@/components/Table';
import { FormSchema } from '/@/components/Form';
import { BasicColumn } from '/@/components/Table/src/types/table';

export function getSearchColumns(): Partial<FormProps> {
  return {
    labelWidth: 100,
    schemas: [
      {
        field: `systemId`,
        label: `系统id`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
        componentProps: {
          placeholder: '请输入系统id',
        },
      },
      {
        field: `systemName`,
        label: `系统名称`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
      },
      {
        field: `secret`,
        label: `秘钥`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
      },
      {
        field: `status`,
        label: `系统状态`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
      },
      {
        field: `remark`,
        label: `备注信息`,
        component: 'Input',
        colProps: {
          xl: 12,
          xxl: 6,
        },
      },
    ],
  };
}

export function getTableColumns(): BasicColumn[] {
  return [
    {
      title: '系统编号',
      dataIndex: 'systemId',
      resizable: true,
      width: 80,
    },
    {
      title: '系统名称',
      dataIndex: 'systemName',
      resizable: true,
      width: 60,
    },
    {
      title: '秘钥',
      dataIndex: 'secret',
      resizable: true,
      width: 120,
    },
    {
      title: '系统状态',
      // width: 80,
      // sorter: true,
      dataIndex: 'status',
      resizable: true,
      width: 40,
    },
    {
      title: '创建人',
      // sorter: true,
      dataIndex: 'createBy',
      resizable: true,
      width: 40,
    },
    {
      title: '创建时间',
      // sorter: true,
      dataIndex: 'createTime',
      resizable: true,
      width: 100,
    },
    {
      title: '更新人',
      // sorter: true,
      dataIndex: 'updateBy',
      resizable: true,
      width: 40,
    },
    {
      title: '更新时间',
      // sorter: true,
      dataIndex: 'updateTime',
      resizable: true,
      width: 100,
    },
    {
      title: '备注信息',
      // sorter: true,
      dataIndex: 'remark',
      resizable: true,
      width: 120,
    },
  ];
}

export function getModalFormColumns(): FormSchema[] {
  return [
    {
      field: `id`,
      component: 'Input',
      label: `编号`,
      show: false,
    },
    {
      field: `systemId`,
      label: `系统编号`,
      component: 'Input',
      colProps: {
        span: 24,
      },
      slot: 'systemIdSlot',
      componentProps: {
        placeholder: '请输入系统id',
      },
    },
    {
      field: `systemName`,
      label: `系统名称`,
      component: 'Input',
      colProps: {
        span: 24,
      },
    },
    {
      field: `secret`,
      label: `秘钥`,
      component: 'Input',
      colProps: {
        span: 24,
      },
    },
    {
      field: `status`,
      label: `系统状态`,
      component: 'Input',
      colProps: {
        span: 24,
      },
    },
    {
      field: `remark`,
      label: `备注信息`,
      component: 'InputTextArea',
      colProps: {
        span: 24,
      },
      defaultValue: null,
    },
  ];
}
