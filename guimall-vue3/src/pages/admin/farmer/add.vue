<template>
  <div class="p-2 box">

    <a-card :bordered="false" class="mb-5">
      <div class="flex flex-wrap items-center gap-4">
        <a-button class="flex items-center gap-1" @click="goBack">
          <ArrowLeftOutlined />
          杩斿洖鍒楄〃
        </a-button>
        <span class="text-base font-semibold">鏂板鍐滄埛</span>
      </div>
    </a-card>

    <a-card :bordered="false" title="鍩烘湰淇℃伅">
      <a-form
        ref="formRef"
        :model="form"
        :rules="rules"
        layout="horizontal"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 14 }"
      >
        <a-form-item label="鍐滄埛濮撳悕" name="name" :required="true">
          <a-input v-model:value="form.name" placeholder="请输入姓名" allow-clear />
        </a-form-item>

        <a-form-item label="手机号" name="phone" :required="true">
          <a-input v-model:value="form.phone" placeholder="璇疯緭鍏ユ墜鏈哄彿" maxlength="11" allow-clear />
        </a-form-item>

        <a-form-item label="韬唤璇佸彿" name="idCard">
          <a-input v-model:value="form.idCard" placeholder="閫夊～" allow-clear />
        </a-form-item>

        <a-form-item label="鍐滃満鍚嶇О" name="farmName">
          <a-input v-model:value="form.farmName" placeholder="如：临桂金桔合作社" allow-clear />
        </a-form-item>

        <a-form-item label="所在地区" name="area">
          <a-cascader
            v-model:value="areaValue"
            :options="areaOptions"
            placeholder="请选择省/市/区"
            :show-search="{ filter }"
            @change="handleAreaChange"
            style="width: 100%"
          />
        </a-form-item>

        <a-form-item label="璇︾粏鍦板潃" name="detailAddress">
          <a-input v-model:value="form.detailAddress" placeholder="街道/乡镇/村信息" allow-clear />
        </a-form-item>

        <a-form-item label="鍏宠仈浜у湴" name="originIds">
          <a-select
            v-model:value="form.originIds"
            mode="multiple"
            placeholder="璇烽€夋嫨璇ュ啘鎴锋墍鍦ㄧ殑浜у湴锛堝彲澶氶€夛級"
            class="w-full"
            allow-clear
            show-search
            option-filter-prop="label"
          >
            <a-select-option v-for="o in originOptions" :key="o.id" :value="o.id" :label="o.originName">
              {{ o.originName }} ({{ o.province }}{{ o.city }})
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="澶村儚">
          <a-upload
            :max-count="1"
            list-type="picture-card"
            :file-list="avatarFileList"
            :custom-request="handleAvatarUpload"
            @remove="handleAvatarRemove"
            accept="image/*"
          >
            <div v-if="avatarFileList.length === 0">
              <PlusOutlined />
              <div class="mt-2">涓婁紶澶村儚</div>
            </div>
          </a-upload>
        </a-form-item>

        <a-form-item label="主要农产品" name="mainProduct">
          <a-input v-model:value="form.mainProduct" placeholder="濡傦細閲戞銆佺綏姹夋灉" allow-clear />
        </a-form-item>
        <a-form-item label="认证类型" name="certType">
          <a-input v-model:value="form.certType" placeholder="如：地理标志,绿色食品" allow-clear />
        </a-form-item>

        <a-form-item label="认证说明" name="certDesc">
          <a-textarea
            v-model:value="form.certDesc"
            :rows="2"
            placeholder="如：国家地理标志保护产品"
            allow-clear
          />
        </a-form-item>

        <a-form-item label="状态" name="status">
          <a-select v-model:value="form.status" placeholder="璇烽€夋嫨" class="w-full">
            <a-select-option :value="1">鍚敤</a-select-option>
            <a-select-option :value="0">绂佺敤</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="简介" name="description">
          <a-textarea
            v-model:value="form.description"
            :rows="4"
            placeholder="閫夊～锛氱妞嶅搧绫汇€佸熀鍦颁粙缁嶇瓑"
            allow-clear
          />
        </a-form-item>
      </a-form>

      <div class="mt-6 flex justify-center gap-3">
        <a-button type="primary" @click="handleSubmit">鎻愪氦</a-button>
        <a-button @click="goBack">鍙栨秷</a-button>
      </div>
    </a-card>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { ArrowLeftOutlined, PlusOutlined } from '@ant-design/icons-vue'
import { createFarmer } from '@/api/admin/farmer'
import { fetchTraceOriginOptions } from '@/api/admin/traceOrigin'
import { uploadFile } from '@/api/admin/upload'
import { chinaAreaData } from '@/utils/chinaArea'
import regionData from '@/utils/regionData'

const router = useRouter()
const formRef = ref()

const areaOptions = regionData
const areaValue = ref([])
const originOptions = ref([])
const avatarFileList = ref([])

const handleAvatarUpload = async ({ file, onSuccess, onError }) => {
  try {
    const res = await uploadFile(file)
    if (res.success) {
      form.avatar = res.data
      avatarFileList.value = [{ uid: '-1', name: file.name, status: 'done', url: res.data }]
      onSuccess(res)
    } else {
      message.error(res.message || '涓婁紶澶辫触')
      onError(new Error(res.message))
    }
  } catch (e) {
    message.error('涓婁紶澶辫触')
    onError(e)
  }
}
const handleAvatarRemove = () => {
  form.avatar = ''
  avatarFileList.value = []
}

const form = reactive({
  name: '',
  phone: '',
  idCard: '',
  farmName: '',
  province: '',
  city: '',
  region: '',
  detailAddress: '',
  avatar: '',
  mainProduct: '',
  certType: '',
  certDesc: '',
  certPic: '',
  status: 1,
  description: '',
  originIds: []
})

const rules = {
  name: [{ required: true, message: '请输入农户姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '璇疯緭鍏ユ墜鏈哄彿', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '璇疯緭鍏ユ纭殑11浣嶆墜鏈哄彿', trigger: 'blur' }
  ]
}

// 绾ц仈閫夋嫨鍣ㄦ悳绱㈣繃婊?
const filter = (inputValue, path) => {
  return path.some(option => option.label.toLowerCase().indexOf(inputValue.toLowerCase()) > -1)
}

// 澶勭悊鍦板尯閫夋嫨鍙樺寲
const handleAreaChange = (value) => {
  if (value && value.length === 3) {
    form.province = value[0]
    form.city = value[1]
    form.region = value[2]
  } else {
    form.province = ''
    form.city = ''
    form.region = ''
  }
}

const goBack = () => {
  router.push('/admin/farmer')
}

onMounted(async () => {
  const rsp = await fetchTraceOriginOptions()
  if (rsp?.success) {
    originOptions.value = rsp.data || []
  }
})

// 鎻愪氦鎸夐挳鍏ュ弬涓庡悗绔?CreateFarmerReqVO 瀛楁涓ユ牸瀵归綈
const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  // 楠岃瘉鍦板尯鏄惁宸查€夋嫨
  if (!form.province || !form.city || !form.region) {
    message.warning('请选择所在地区')
    return
  }

  const reqVO = {
    name: form.name.trim(),
    phone: form.phone.trim(),
    idCard: form.idCard?.trim() || '',
    avatar: form.avatar?.trim() || '',
    farmName: form.farmName?.trim() || '',
    province: form.province?.trim() || '',
    city: form.city?.trim() || '',
    region: form.region?.trim() || '',
    detailAddress: form.detailAddress?.trim() || '',
    mainProduct: form.mainProduct?.trim() || '',
    certType: form.certType?.trim() || '',
    certDesc: form.certDesc?.trim() || '',
    certPic: form.certPic?.trim() || '',
    description: form.description?.trim() || '',
    status: form.status,
    originIds: form.originIds || []
  }
  const rsp = await createFarmer(reqVO)
  if (!rsp?.success) {
    message.error(rsp?.message || '鏂板鍐滄埛澶辫触')
    return
  }
  message.success('鏂板鎴愬姛')
  goBack()
}
</script>


