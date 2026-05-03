<template>
  <div class="p-2 box">
    <a-card :bordered="false" class="mb-5">
      <div class="flex flex-wrap items-center gap-4">
        <a-button class="flex items-center gap-1" @click="goBack">
          <ArrowLeftOutlined />
          返回列表
        </a-button>
        <span class="text-base font-semibold">新增商品</span>
      </div>
    </a-card>

    <a-card :bordered="false" title="基本信息">
      <a-form
        ref="formRef"
        :model="form"
        :rules="rules"
        layout="horizontal"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 14 }"
      >
        <a-form-item name="name" label="商品名称" :required="true">
          <a-input v-model:value="form.name" placeholder="请输入商品名称" />
        </a-form-item>

        <a-form-item name="productCategoryId" label="商品分类" :required="true">
          <a-select v-model:value="form.productCategoryId" placeholder="请选择分类" class="w-full" allow-clear>
            <a-select-option v-for="item in categoryOptions" :key="item.id" :value="item.id">
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item name="farmerId" label="关联农户" :required="true">
          <a-select v-model:value="form.farmerId" placeholder="请选择农户" class="w-full" allow-clear>
            <a-select-option v-for="item in farmerOptions" :key="item.id" :value="item.id">
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item name="productSn" label="商品货号" :required="true">
          <a-input v-model:value="form.productSn" placeholder="请输入商品货号" />
        </a-form-item>

        <a-form-item label="商品副标题">
          <a-input v-model:value="form.subTitle" placeholder="请输入商品副标题" />
        </a-form-item>

        <a-form-item label="商品主图">
          <a-upload
            :max-count="1"
            list-type="picture-card"
            :file-list="picFileList"
            :custom-request="handlePicUpload"
            @remove="handlePicRemove"
            accept="image/*"
          >
            <div v-if="picFileList.length === 0">
              <PlusOutlined />
              <div class="mt-2">上传图片</div>
            </div>
          </a-upload>
        </a-form-item>

        <a-form-item label="商品相册">
          <a-upload
            list-type="picture-card"
            :file-list="albumFileList"
            :custom-request="handleAlbumUpload"
            @remove="handleAlbumRemove"
            accept="image/*"
            multiple
          >
            <div>
              <PlusOutlined />
              <div class="mt-2">上传图片</div>
            </div>
          </a-upload>
        </a-form-item>

        <a-form-item name="price" label="销售价格" :required="true">
          <a-input-number v-model:value="form.price" :min="0" :precision="2" class="w-full" />
        </a-form-item>

        <a-form-item label="市场价格">
          <a-input-number v-model:value="form.originalPrice" :min="0" :precision="2" class="w-full" />
        </a-form-item>

        <a-form-item name="stock" label="库存" :required="true">
          <a-input-number v-model:value="form.stock" :min="0" class="w-full" />
        </a-form-item>

        <a-form-item label="单位">
          <a-input v-model:value="form.unit" placeholder="如：斤、箱" />
        </a-form-item>

        <a-form-item label="上架状态">
          <a-switch v-model:checked="publishChecked" />
        </a-form-item>

        <a-form-item label="助农商品">
          <a-switch v-model:checked="aidAgricultureChecked" />
        </a-form-item>

        <a-form-item label="商品描述">
          <RichEditor v-model="form.detailHtml" />
        </a-form-item>
      </a-form>
    </a-card>

    <a-card :bordered="false" title="商品参数" class="mt-5">
      <a-alert
        class="mb-4"
        type="info"
        show-icon
        banner
        message="从参数字典中选择参数。如需新增参数，请前往【参数管理】页面。"
      />

      <a-spin :spinning="paramLoading">
        <a-table
          :dataSource="paramRows"
          :columns="paramColumns"
          rowKey="paramId"
          :pagination="paramPagination"
          @change="handleParamTableChange"
          bordered
          size="small"
        />
      </a-spin>
    </a-card>

    <a-card title="SKU库存管理（必填）" class="mt-5">
      <a-alert
        class="mb-4"
        type="warning"
        show-icon
        banner
        message="至少添加一个SKU规格，否则用户无法下单购买。"
      />

      <div class="mb-4">
        <a-button type="primary" @click="handleAddSku">新增SKU</a-button>
      </div>

      <a-table
        :dataSource="skuRows"
        :columns="skuColumns"
        rowKey="tempKey"
        :pagination="false"
        bordered
      />
    </a-card>

    <div class="fixed-bottom-bar">
      <a-button type="primary" @click="handleSubmit">提交</a-button>
      <a-button @click="goBack">取消</a-button>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, h, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message, Input, InputNumber, Button, Popconfirm, Checkbox } from 'ant-design-vue'
import { ArrowLeftOutlined, PlusOutlined } from '@ant-design/icons-vue'
import { createProduct } from '@/api/admin/product'
import { saveSkuList } from '@/api/admin/productSku'
import { fetchProductCategoryOptions } from '@/api/admin/productCategory'
import { fetchFarmerOptions } from '@/api/admin/farmer'
import { uploadFile } from '@/api/admin/upload'
import { fetchParamDefinitions } from '@/api/admin/paramDefinition'
import RichEditor from '@/components/RichEditor.vue'

const router = useRouter()
const formRef = ref(null)
const categoryOptions = ref([])
const farmerOptions = ref([])
const publishChecked = ref(false)
const aidAgricultureChecked = ref(false)
const picFileList = ref([])
const albumFileList = ref([])

const skuRows = ref([])

const paramRows = ref([])
const paramLoading = ref(false)
const paramPagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: total => `共 ${total} 条`
})
const selectedParamIds = ref(new Set())

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  productCategoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  farmerId: [{ required: true, message: '请选择关联农户', trigger: 'change' }],
  productSn: [{ required: true, message: '请输入商品货号', trigger: 'blur' }],
  price: [{ required: true, message: '请输入销售价格', trigger: 'change' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'change' }]
}

const form = reactive({
  name: '',
  productCategoryId: undefined,
  farmerId: undefined,
  productSn: '',
  subTitle: '',
  pic: '',
  albumPics: '',
  albumPicList: [],
  price: undefined,
  originalPrice: undefined,
  stock: 0,
  unit: '斤',
  publishStatus: 0,
  isAidAgriculture: 0,
  detailHtml: ''
})

const goBack = () => {
  router.push('/admin/pms/product')
}

const handlePicUpload = async ({ file, onSuccess, onError }) => {
  try {
    const res = await uploadFile(file)
    if (res.success) {
      form.pic = res.data
      picFileList.value = [{ uid: '-1', name: file.name, status: 'done', url: res.data }]
      onSuccess(res)
    } else {
      message.error(res.message || '上传失败')
      onError(new Error(res.message))
    }
  } catch (e) {
    message.error('上传失败')
    onError(e)
  }
}

const handlePicRemove = () => {
  form.pic = ''
  picFileList.value = []
}

const syncAlbumFields = () => {
  const list = albumFileList.value
    .map(item => item.url || item.response?.data || '')
    .map(item => String(item).trim())
    .filter(Boolean)
  form.albumPicList = list
  form.albumPics = list.join(',')
}

const handleAlbumUpload = async ({ file, onSuccess, onError }) => {
  try {
    const res = await uploadFile(file)
    if (res.success) {
      albumFileList.value = [
        ...albumFileList.value,
        {
          uid: `${Date.now()}-${Math.random()}`,
          name: file.name,
          status: 'done',
          url: res.data
        }
      ]
      syncAlbumFields()
      onSuccess(res)
    } else {
      message.error(res.message || '上传失败')
      onError(new Error(res.message))
    }
  } catch (e) {
    message.error('上传失败')
    onError(e)
  }
}

const handleAlbumRemove = file => {
  albumFileList.value = albumFileList.value.filter(item => item.uid !== file.uid)
  syncAlbumFields()
}

const paramColumns = [
  {
    title: '选择',
    width: '10%',
    align: 'center',
    customRender: ({ record }) =>
      h(Checkbox, {
        checked: record.selected,
        onChange: e => {
          record.selected = e.target.checked
          if (e.target.checked) {
            selectedParamIds.value.add(record.paramId)
          } else {
            selectedParamIds.value.delete(record.paramId)
          }
        }
      })
  },
  {
    title: '参数名',
    dataIndex: 'key',
    width: '30%'
  },
  {
    title: '参数值',
    dataIndex: 'value',
    width: '60%'
  }
]

const loadAllParams = async () => {
  paramLoading.value = true
  try {
    const rsp = await fetchParamDefinitions({
      current: paramPagination.value.current,
      size: paramPagination.value.pageSize
    })

    if (!rsp?.success) return

    const defs = rsp?.data || []
    paramPagination.value.total = rsp?.total || 0

    paramRows.value = defs.map(d => ({
      paramId: d.id,
      key: d.paramName,
      value: d.paramValue,
      selected: selectedParamIds.value.has(d.id)
    }))
  } finally {
    paramLoading.value = false
  }
}

const handleParamTableChange = pagination => {
  paramPagination.value.current = pagination.current
  paramPagination.value.pageSize = pagination.pageSize
  loadAllParams()
}

const buildProductParams = () => {
  return Array.from(selectedParamIds.value).map(paramId => ({ paramId }))
}

const skuColumns = [
  {
    title: '规格名称',
    width: '15%',
    customRender: ({ record }) =>
      h(Input, {
        value: record.specKey,
        onChange: e => (record.specKey = e.target.value),
        placeholder: '如：重量'
      })
  },
  {
    title: '规格值',
    width: '15%',
    customRender: ({ record }) =>
      h(Input, {
        value: record.specValue,
        onChange: e => (record.specValue = e.target.value),
        placeholder: '如：3斤'
      })
  },
  {
    title: 'SKU编码',
    width: '15%',
    customRender: ({ record }) =>
      h(Input, {
        value: record.skuCode,
        onChange: e => (record.skuCode = e.target.value),
        placeholder: '必填'
      })
  },
  {
    title: '价格',
    width: '12%',
    customRender: ({ record }) =>
      h(InputNumber, {
        value: record.price,
        onChange: v => (record.price = v),
        min: 0,
        precision: 2,
        placeholder: '必填'
      })
  },
  {
    title: '库存',
    width: '12%',
    customRender: ({ record }) =>
      h(InputNumber, {
        value: record.stock,
        onChange: v => (record.stock = v),
        min: 0,
        placeholder: '必填'
      })
  },
  {
    title: '促销价',
    width: '12%',
    customRender: ({ record }) =>
      h(InputNumber, {
        value: record.promotionPrice,
        onChange: v => (record.promotionPrice = v),
        min: 0,
        precision: 2,
        placeholder: '选填'
      })
  },
  {
    title: '预警库存',
    width: '10%',
    customRender: ({ record }) =>
      h(InputNumber, {
        value: record.lowStock,
        onChange: v => (record.lowStock = v),
        min: 0,
        placeholder: '选填'
      })
  },
  {
    title: '操作',
    width: '9%',
    align: 'center',
    customRender: ({ record }) =>
      h(
        Popconfirm,
        {
          title: '确认删除？',
          onConfirm: () => handleDeleteSku(record)
        },
        {
          default: () => h(Button, { danger: true, size: 'small' }, () => '删除')
        }
      )
  }
]

const handleAddSku = () => {
  skuRows.value.push({
    tempKey: Date.now() + Math.random(),
    skuCode: '',
    specKey: '',
    specValue: '',
    price: undefined,
    stock: 0,
    promotionPrice: undefined,
    lowStock: 0,
    pic: ''
  })
}

const handleDeleteSku = record => {
  skuRows.value = skuRows.value.filter(r => r.tempKey !== record.tempKey)
}

onMounted(async () => {
  const [categoryRsp, farmerRsp] = await Promise.all([fetchProductCategoryOptions(), fetchFarmerOptions()])
  categoryOptions.value = categoryRsp?.data || []
  farmerOptions.value = farmerRsp?.data || []

  await loadAllParams()
})

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }

  if (skuRows.value.length === 0) {
    message.error('请至少添加一个SKU规格')
    return
  }

  for (let i = 0; i < skuRows.value.length; i++) {
    const sku = skuRows.value[i]
    if (!sku.specKey || !String(sku.specKey).trim()) {
      message.error(`第 ${i + 1} 个SKU的规格名称不能为空`)
      return
    }
    if (!sku.specValue || !String(sku.specValue).trim()) {
      message.error(`第 ${i + 1} 个SKU的规格值不能为空`)
      return
    }
    if (!sku.skuCode || !sku.skuCode.trim()) {
      message.error(`第 ${i + 1} 个SKU的编码不能为空`)
      return
    }
    if (!sku.price || sku.price <= 0) {
      message.error(`第 ${i + 1} 个SKU的价格必须大于0`)
      return
    }
    if (sku.stock === undefined || sku.stock === null || sku.stock < 0) {
      message.error(`第 ${i + 1} 个SKU的库存不能为空`)
      return
    }
  }

  const productData = {
    productCategoryId: form.productCategoryId,
    farmerId: form.farmerId,
    name: form.name.trim(),
    subTitle: form.subTitle?.trim() || null,
    productSn: form.productSn.trim(),
    pic: form.pic?.trim() || null,
    albumPicList: form.albumPicList || [],
    albumPics: form.albumPics || null,
    detailHtml: form.detailHtml || null,
    description: form.detailHtml?.trim() || null,
    price: form.price,
    marketPrice: form.originalPrice ?? null,
    stock: form.stock,
    unit: form.unit?.trim() || null,
    isAidAgriculture: aidAgricultureChecked.value ? 1 : 0,
    productParams: buildProductParams(),
    skuStockList: skuRows.value.map(sku => ({
      skuCode: sku.skuCode.trim(),
      price: sku.price,
      stock: sku.stock,
      promotionPrice: sku.promotionPrice || null,
      lowStock: sku.lowStock || 0,
      pic: sku.pic || '',
      specKey: String(sku.specKey || '').trim(),
      specValue: String(sku.specValue || '').trim(),
      specs: [{ specKey: String(sku.specKey || '').trim(), specValue: String(sku.specValue || '').trim() }]
    }))
  }

  const createRsp = await createProduct(productData)
  const rawProductId = createRsp?.data?.id ?? createRsp?.data?.productId ?? createRsp?.data
  const createdProductId = Number(rawProductId)
  if (!Number.isFinite(createdProductId) || createdProductId <= 0) {
    message.error('商品已创建，但未拿到商品ID，SKU未保存。请联系后端检查创建接口返回值。')
    return
  }

  const skuPayload = skuRows.value.map(sku => ({
    productId: createdProductId,
    skuCode: sku.skuCode.trim(),
    price: sku.price,
    stock: sku.stock,
    promotionPrice: sku.promotionPrice || null,
    lowStock: sku.lowStock || 0,
    pic: sku.pic || '',
    specs: [{ specKey: String(sku.specKey || '').trim(), specValue: String(sku.specValue || '').trim() }]
  }))

  const skuRsp = await saveSkuList(createdProductId, skuPayload)
  if (!skuRsp?.success) {
    message.error('SKU保存失败')
    return
  }

  if (publishChecked.value) {
    message.info('商品已创建，请在商品列表点击“上架”开关完成上架。')
  } else {
    message.success('创建成功')
  }

  Object.assign(form, {
    name: '',
    productCategoryId: undefined,
    farmerId: undefined,
    productSn: '',
    subTitle: '',
    pic: '',
    albumPics: '',
    albumPicList: [],
    price: undefined,
    originalPrice: undefined,
    stock: 0,
    unit: '斤',
    publishStatus: 0,
    isAidAgriculture: 0,
    detailHtml: ''
  })

  publishChecked.value = false
  aidAgricultureChecked.value = false
  picFileList.value = []
  albumFileList.value = []
  skuRows.value = []
  selectedParamIds.value.clear()
  await loadAllParams()

  goBack()
}
</script>

<style scoped>
.fixed-bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 99;
  display: flex;
  justify-content: center;
  gap: 12px;
  padding: 12px 24px;
  background: #fff;
  border-top: 1px solid #f0f0f0;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.06);
}

.box {
  padding-bottom: 72px;
}

:deep(.ant-input),
:deep(.ant-input-number),
:deep(.ant-select-selector),
:deep(.ant-input-affix-wrapper) {
  border: 1px solid #d9d9d9 !important;
  border-radius: 6px !important;
}

:deep(.ant-input:hover),
:deep(.ant-select-selector:hover),
:deep(.ant-input-number:hover) {
  border-color: #bfbfbf !important;
}

:deep(.ant-input:focus),
:deep(.ant-input-focused),
:deep(.ant-select-focused .ant-select-selector),
:deep(.ant-input-number-focused) {
  border-color: #4096ff !important;
  box-shadow: 0 0 0 2px rgba(64, 150, 255, 0.15);
}

:deep(.ant-select-selector) {
  height: 32px !important;
  display: flex;
  align-items: center;
}

:deep(.ant-input-number) {
  width: 100%;
}

:deep(.ant-table) {
  border-radius: 8px;
  overflow: hidden;
}
</style>
