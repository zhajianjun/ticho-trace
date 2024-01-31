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
          <template #systemIdSlot="{ model, field }">
            <a-input
              v-model:value="model[field]"
              :disabled="!isAddProxy"
              placeholder="请输入账号"
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
  import { Space } from 'ant-design-vue';
  import { systemPage, saveySystem, modifySystem, delSystem } from '/@/api/system/system';
  import { BasicForm, useForm } from '/@/components/Form';
  import { message } from 'ant-design-vue';

  export default defineComponent({
    components: {
      BasicForm,
      TableAction,
      BasicTable,
      BasicModal,
      ASpace: Space,
    },
    setup() {
      const checkedKeys = ref<Array<string | number>>([]);
      const modelTemp = ref<Recordable | null>({});
      const modelTitle = ref<string>();
      const checkedKeysCount = ref<string | number>();
      const wrapEl = ref<ElRef>(null);
      const isAddProxy = ref<boolean>(true);
      const [registerTable, { reload }] = useTable({
        title: '',
        api: systemPageProxy,
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

      function systemPageProxy(params) {
        return systemPage(params).catch((err) => {
          message.error(err.message);
        });
      }

      function openUserDialogue(isAdd: boolean, record: Recordable | null) {
        isAddProxy.value = isAdd;
        if (isAddProxy.value) {
          modelTitle.value = '新增';
          modelTemp.value = {};
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
          resetFields();
          return;
        }
        setFieldsValue(modelTemp.value);
      }

      function handleSubmit() {
        openWrapLoading();
        const user = getFieldsValue();
        if (isAddProxy.value) {
          saveySystem(user)
            .then(() => {
              reload();
              openModal(false);
              resetFields();
            })
            .finally(() => {
              closeWrapLoading();
            });
        } else {
          modifySystem(user)
            .then(() => {
              reload();
              openModal(false);
              resetFields();
            })
            .finally(() => {
              closeWrapLoading();
            });
        }
      }

      function deleteUser(record: Recordable) {
        delSystem(record.id)
          .then(() => {
            reload();
          })
          .catch(() => {});
      }

      function closeFunc() {
        resetFields();
        return true;
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
        isAddProxy,
        handleResizeColumn,
        systemPageProxy,
      };
    },
  });
</script>
