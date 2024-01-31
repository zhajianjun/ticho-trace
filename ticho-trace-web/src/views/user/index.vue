<template>
  <div>
    <BasicTable @register="registerTable" @resize-column="handleResizeColumn">
      <template #tableTitle>
        <a-space :size="10">
          <a-button
            ghost
            type="primary"
            preIcon="ant-design:plus-circle-outlined"
            @click="openUserDialogue(true, null)"
            >新增
          </a-button>
          <a-popconfirm placement="right" ok-text="是" cancel-text="否" @confirm="deleteUserBatch">
            <template #title>
              <p>是删除{{ checkedKeysCount }}数据?</p>
            </template>
            <a-button ghost type="danger" preIcon="ant-design:delete-outlined"
              >删除{{ checkedKeysCount }}
            </a-button>
          </a-popconfirm>
        </a-space>
      </template>
      <template #bodyCell="{ column, record }">
        <!--suppress TypeScriptUnresolvedReference -->
        <template v-if="column.key === 'action'">
          <TableAction
            stopButtonPropagation
            :actions="[
              {
                label: '',
                icon: 'material-symbols:edit-note-sharp',
                onClick: openUserDialogue.bind(null, false, record),
              },
              {
                label: '',
                icon: 'ic:outline-delete-outline',
                popConfirm: {
                  title: '是否删除？',
                  okText: '是',
                  cancelText: '否',
                  confirm: deleteUser.bind(null, record),
                },
              },
            ]"
          />
        </template>
      </template>
    </BasicTable>
    <BasicModal
      v-bind="$attrs"
      @register="registerModel"
      :title="modelTitle"
      :closeFunc="closeFunc"
      :maskClosable="false"
    >
      <div class="pt-3px pr-3px" ref="wrapEl">
        <BasicForm @register="registerForm">
          <template #usernameSlot="{ model, field }">
            <a-input
              v-model:value="model[field]"
              :disabled="!isAddProxy"
              placeholder="请输入账号"
            />
          </template>
          <template #systemIdsSlot="{ model, field }">
            <a-select
              mode="multiple"
              style="width: 100%"
              placeholder="请选择系统信息"
              :max-tag-text-length="10"
              v-model:value="model[field]"
              :options="systemAllProxy"
            />
          </template>
        </BasicForm>
      </div>
      <template #footer>
        <div style="text-align: center" ref="wrapEl">
          <a-space :size="10">
            <a-button type="primary" @click="resetFieldsProxy">重置</a-button>
            <a-button type="primary" @click="handleSubmit">提交</a-button>
          </a-space>
        </div>
      </template>
    </BasicModal>
  </div>
</template>
<script lang="ts">
  import { defineComponent, ref, nextTick } from 'vue';
  import { BasicTable, TableAction, useTable } from '/@/components/Table';
  import { BasicModal, useModal } from '/@/components/Modal';
  import { useLoading } from '/@/components/Loading';
  import { getTableColumns, getSearchColumns, getModalFormColumns } from './tableData';
  import { Space, Popconfirm, message, Select } from 'ant-design-vue';
  import { userPage, saveyUser, modifyUser, delUser, delUserBatch } from '/@/api/sys/user';
  import { BasicForm, useForm } from '/@/components/Form';
  import { systemlistAll } from '/@/api/system/system';

  export default defineComponent({
    components: {
      BasicForm,
      TableAction,
      BasicTable,
      BasicModal,
      ASpace: Space,
      ASelect: Select,
      APopconfirm: Popconfirm,
    },
    setup() {
      const checkedKeys = ref<Array<string | number>>([]);
      const modelTemp = ref<Recordable | null>({ systemIds: [] });
      const modelTitle = ref<string>();
      const checkedKeysCount = ref<string | number>();
      const wrapEl = ref<ElRef>(null);
      const isAddProxy = ref<boolean>(true);
      const systemAllProxy = ref<Array<any>>([]);
      const [registerTable, { reload }] = useTable({
        title: '',
        api: userPageProxy,
        columns: getTableColumns(),
        useSearchForm: true,
        formConfig: getSearchColumns(),
        showTableSetting: true,
        tableSetting: { fullScreen: true },
        showIndexColumn: false,
        rowKey: 'id',
        rowSelection: {
          type: 'checkbox',
          onSelect: onSelect,
          onSelectAll: onSelectAll,
        },
        actionColumn: {
          width: 40,
          title: '操作',
          dataIndex: 'action',
          fixed: 'right',
          // slots: { customRender: 'action' },
        },
        striped: true,
        canColDrag: true,
        pagination: {
          pageSize: 15,
          pageSizeOptions: ['10', '15', '20', '30', '50', '100'],
          position: ['bottomCenter'],
          size: 'large',
        },
        loading: true,
        bordered: true,
      });

      const [registerModel, { openModal }] = useModal();

      const [registerForm, { resetFields, setFieldsValue, getFieldsValue }] = useForm({
        labelWidth: 120,
        schemas: getModalFormColumns(),
        showActionButtonGroup: false,
        actionColOptions: {
          span: 24,
        },
      });

      const [openWrapLoading, closeWrapLoading] = useLoading({
        target: wrapEl,
        props: {
          tip: '加载中...',
          absolute: true,
        },
      });

      function userPageProxy(params) {
        return userPage(params).catch((err) => {
          message.error(err.message);
        });
      }

      function openUserDialogue(isAdd: boolean, record: Recordable | null) {
        isAddProxy.value = isAdd;
        if (isAddProxy.value) {
          modelTitle.value = '新增';
          modelTemp.value = { systemIds: [] };
        } else {
          modelTitle.value = '修改';
          modelTemp.value = record;
        }
        openModal(true);
        nextTick(() => {
          setFieldsValue(modelTemp.value);
        });
      }

      function resetFieldsProxy() {
        if (modelTitle.value === '新增') {
          closeFunc();
          return;
        }
        setFieldsValue(modelTemp.value);
      }

      function closeFunc() {
        resetFields();
        setFieldsValue({ systemIds: [] });
        return true;
      }

      function handleSubmit() {
        openWrapLoading();
        const user = getFieldsValue();
        if (isAddProxy.value) {
          saveyUser(user)
            .then(() => {
              reload();
              openModal(false);
              closeFunc();
            })
            .finally(() => {
              closeWrapLoading();
            });
        } else {
          modifyUser(user)
            .then(() => {
              reload();
              openModal(false);
              closeFunc();
            })
            .finally(() => {
              closeWrapLoading();
            });
        }
      }

      function deleteUser(record: Recordable) {
        delUser(record.id)
          .then(() => {
            reload();
          })
          .catch(() => {});
      }

      function deleteUserBatch() {
        let length = checkedKeys.value.length;
        if (length <= 0) {
          message.warn('请选择数据');
          return;
        }
        delUserBatch(checkedKeys.value.join(','))
          .then(() => {
            onSelectReset();
            reload();
          })
          .catch(() => {});
      }

      function onSelectReset() {
        checkedKeys.value = [];
        checkedKeysCount.value = '';
      }

      function onSelect(record, selected) {
        if (selected) {
          checkedKeys.value = [...checkedKeys.value, record.id];
        } else {
          checkedKeys.value = checkedKeys.value.filter((id) => id !== record.id);
        }
        checkedKeysCount.value = checkedKeys.value.length > 0 ? checkedKeys.value.length : '';
      }

      function onSelectAll(selected, _selectedRows, changeRows) {
        const changeIds = changeRows.map((item) => item.id);
        if (selected) {
          checkedKeys.value = [...checkedKeys.value, ...changeIds];
        } else {
          checkedKeys.value = checkedKeys.value.filter((id) => {
            return !changeIds.includes(id);
          });
        }
        checkedKeysCount.value = checkedKeys.value.length > 0 ? checkedKeys.value.length : '';
      }

      function handleResizeColumn(w, col) {
        col.width = w;
      }

      systemlistAll().then((res) => {
        systemAllProxy.value = res.map((item) => {
          return {
            label: item.systemName,
            value: item.systemId,
          };
        });
      });

      return {
        registerTable,
        registerModel,
        registerForm,
        checkedKeys,
        checkedKeysCount,
        onSelect,
        onSelectAll,
        openUserDialogue,
        closeFunc,
        resetFieldsProxy,
        handleSubmit,
        modelTitle,
        wrapEl,
        deleteUser,
        deleteUserBatch,
        isAddProxy,
        handleResizeColumn,
        userPageProxy,
        systemAllProxy,
      };
    },
  });
</script>
