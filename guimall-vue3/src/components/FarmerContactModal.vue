<template>
  <div
    v-if="modelValue"
    class="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm"
    @click.self="close"
  >
    <div
      class="bg-white rounded-[2.5rem] p-10 shadow-2xl w-full max-w-2xl mx-4 relative max-h-[85vh] overflow-y-auto overflow-x-hidden overscroll-contain farmer-scroll"
      @wheel.stop
    >
      <button
        @click="close"
        class="absolute top-6 right-6 w-10 h-10 bg-stone-100 rounded-xl flex items-center justify-center text-stone-400 hover:bg-stone-200 transition-all"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path d="M6 18L18 6M6 6l12 12" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" />
        </svg>
      </button>

      <div class="flex items-center gap-4 mb-8">
        <FarmerAvatar :src="farmer.avatar" :seed="farmer.name" img-class="w-16 h-16 rounded-2xl border-2 border-emerald-100" />
        <div>
          <h3 class="text-2xl font-black text-stone-900">{{ farmer.name || '签约农户' }}</h3>
          <p class="text-stone-400">{{ farmer.farmName || '暂无基地信息' }}</p>
        </div>
      </div>

      <div class="space-y-4">
        <div v-if="farmer.phone" class="flex items-center gap-4 p-5 bg-emerald-50 rounded-2xl">
          <div class="w-12 h-12 bg-emerald-600 rounded-xl flex items-center justify-center shrink-0">
            <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" />
            </svg>
          </div>
          <div>
            <p class="text-xs text-stone-400 font-bold uppercase tracking-widest mb-0.5">联系电话</p>
            <a :href="'tel:' + farmer.phone" class="text-2xl font-black text-emerald-600 hover:text-emerald-700">{{ farmer.phone }}</a>
          </div>
        </div>
        <div v-else class="p-5 bg-stone-50 rounded-2xl text-center text-stone-400 font-bold">
          暂未提供联系方式
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
          <div class="p-4 bg-stone-50 rounded-2xl">
            <p class="text-xs text-stone-400 font-bold mb-1">所在地区</p>
            <p class="text-sm text-stone-700 font-semibold">{{ farmerLocation || '暂无' }}</p>
          </div>
          <div class="p-4 bg-stone-50 rounded-2xl">
            <p class="text-xs text-stone-400 font-bold mb-1">详细地址</p>
            <p class="text-sm text-stone-700 font-semibold">{{ farmer.detailAddress || '暂无' }}</p>
          </div>
          <div class="p-4 bg-stone-50 rounded-2xl">
            <p class="text-xs text-stone-400 font-bold mb-1">主营产品</p>
            <p class="text-sm text-stone-700 font-semibold">{{ farmer.mainProduct || '暂无' }}</p>
          </div>
          <div class="p-4 bg-stone-50 rounded-2xl">
            <p class="text-xs text-stone-400 font-bold mb-1">认证类型</p>
            <p class="text-sm text-stone-700 font-semibold">{{ farmer.certType || '暂无' }}</p>
          </div>
        </div>

        <div class="p-4 bg-stone-50 rounded-2xl">
          <p class="text-xs text-stone-400 font-bold mb-1">认证说明</p>
          <p class="text-sm text-stone-700">{{ farmer.certDesc || '暂无' }}</p>
        </div>
        <div class="p-4 bg-stone-50 rounded-2xl">
          <p class="text-xs text-stone-400 font-bold mb-1">农户简介</p>
          <p class="text-sm text-stone-700 leading-6">{{ farmer.description || '暂无' }}</p>
        </div>
        <p class="text-xs text-stone-300 text-center">点击电话号码可直接拨打</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, watch } from 'vue'
import FarmerAvatar from '@/components/FarmerAvatar.vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  farmer: {
    type: Object,
    default: () => ({
      name: '',
      farmName: '',
      avatar: '',
      phone: '',
      province: '',
      city: '',
      region: '',
      detailAddress: '',
      mainProduct: '',
      description: '',
      certType: '',
      certDesc: '',
    }),
  },
})

const emit = defineEmits(['update:modelValue'])

const close = () => emit('update:modelValue', false)

const farmerLocation = computed(() => {
  return `${props.farmer?.province || ''}${props.farmer?.city || ''}${props.farmer?.region || ''}`
})

watch(
  () => props.modelValue,
  (open) => {
    document.body.style.overflow = open ? 'hidden' : ''
  }
)

onBeforeUnmount(() => {
  document.body.style.overflow = ''
})
</script>

<style scoped>
.farmer-scroll {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.farmer-scroll::-webkit-scrollbar {
  display: none;
  width: 0;
  height: 0;
}
</style>
