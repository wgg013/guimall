<template>
  <div class="min-h-screen bg-stone-50 font-sans pb-20">
    <!-- 椤堕儴瀵艰埅 (鍏变韩鏍峰紡) -->
    <nav class="bg-white/90 backdrop-blur-md border-b border-stone-200 sticky top-0 z-50">
      <div class="max-w-screen-xl flex items-center justify-between mx-auto p-4">
        <router-link to="/" class="flex items-center space-x-3">
          <div class="w-8 h-8 bg-emerald-600 rounded-lg flex items-center justify-center">
            <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"/></svg>
          </div>
          <span class="text-xl font-bold tracking-tight text-emerald-900 uppercase">Guimall</span>
        </router-link>
        <div class="flex items-center space-x-6">
           <button @click="$router.back()" class="text-stone-500 flex items-center hover:text-emerald-600 transition-colors">
             <svg class="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M10 19l-7-7m0 0l7-7m-7 7h18" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"/></svg>
             杩斿洖
           </button>
           <div class="w-px h-4 bg-stone-200"></div>
           <router-link to="/cart" class="text-stone-500 hover:text-emerald-600 transition-colors flex items-center">
             <a-badge :count="cartStore.cartCount" :offset="[-2, 2]" size="small">
               <svg class="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                 <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 100 4 2 2 0 000-4z" />
               </svg>
             </a-badge>
             璐墿杞?
           </router-link>
           <div class="w-px h-4 bg-stone-200"></div>
           <router-link to="/" class="text-stone-500 hover:text-emerald-600">棣栭〉</router-link>
        </div>
      </div>
    </nav>

    <main class="max-w-screen-xl mx-auto px-4 mt-12">
      <div class="grid grid-cols-1 lg:grid-cols-12 gap-12">
        <!-- 宸︿晶锛氬浘鐗囧睍绀?-->
        <div class="lg:col-span-6">
          <div class="bg-white rounded-[3rem] overflow-hidden shadow-xl border border-stone-100 sticky top-24">
            <img :src="mainDisplayPic" class="w-full aspect-square object-cover transition-transform duration-700 hover:scale-105" />
            <div v-if="albumPicList.length > 0" class="flex p-4 gap-4 overflow-x-auto">
              <img
                v-for="pic in albumPicList"
                :key="pic"
                :src="pic"
                class="w-20 h-20 rounded-2xl object-cover cursor-pointer border-2 transition-all"
                :class="currentPreviewPic === pic ? 'border-emerald-500' : 'border-transparent hover:border-emerald-500'"
                @click="currentPreviewPic = pic"
              />
            </div>
          </div>
        </div>

        <!-- 鍙充晶锛氳鎯呬笌璐拱 -->
        <div class="lg:col-span-6 space-y-8">
          <div>
            <div class="flex items-center space-x-2 mb-4">
              <span class="bg-emerald-100 text-emerald-700 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider">妗傛灄鐗硅壊</span>
              <span class="bg-orange-100 text-orange-700 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider">鍔╁啘鐩翠緵</span>
            </div>
            <h1 class="text-4xl font-black text-stone-900 mb-4 leading-tight">{{ product.name }}</h1>
            <p class="text-lg text-stone-500">{{ product.subTitle }}</p>
          </div>

          <div class="bg-emerald-50 p-8 rounded-[2.5rem] border border-emerald-100 relative overflow-hidden">
             <div class="absolute right-0 top-0 w-32 h-32 bg-emerald-100/50 rounded-full translate-x-10 -translate-y-10 blur-2xl"></div>
             <div class="relative z-10">
               <div class="flex items-baseline space-x-3 mb-2">
                 <span class="text-stone-500 font-bold">浠锋牸</span>
                 <!-- 鍒掔嚎浠锋牸锛氶€変腑SKU鏃舵樉绀篠KU鍘熶环锛屽惁鍒欐樉绀哄晢鍝佸競鍦轰环 -->
                 <span class="text-stone-400 text-lg line-through" v-if="selectedSku ? selectedSku.price : product.marketPrice">
                   楼{{ selectedSku ? selectedSku.price : product.marketPrice }}
                 </span>
                 <!-- 褰撳墠灞曠ず浠凤細SKU閫変腑鏃朵紭鍏堢敤SKU淇冮攢浠凤紝鍚﹀垯鐢ㄥ晢鍝佷績閿€浠凤紝鏈€鍚庣敤SKU/鍟嗗搧鍞环 -->
                 <span class="text-4xl font-black text-emerald-600">
                   楼{{ selectedSku ? (selectedSku.promotionPrice || product.promotionPrice || selectedSku.price) : (product.promotionPrice || product.price) }}
                 </span>
                 <span class="text-stone-400 text-sm">/ {{ product.unit || '件' }}</span>
               </div>
               <!-- 淇冮攢鏍囩 -->
               <div class="flex gap-2 flex-wrap">
                 <span v-if="product.promotionPrice" class="bg-red-100 text-red-600 text-xs font-bold px-3 py-1 rounded-full">闄愭椂淇冮攢</span>
                 <span v-if="product.marketPrice && product.price" class="bg-orange-100 text-orange-600 text-xs font-bold px-3 py-1 rounded-full">
                   鐪?楼{{ (product.marketPrice - (product.promotionPrice || product.price)).toFixed(2) }}
                 </span>
                 <span class="bg-emerald-100 text-emerald-700 text-xs font-bold px-3 py-1 rounded-full">浜у湴鐩翠緵</span>
               </div>
             </div>
          </div>

          <!-- SKU 閫夋嫨 -->
          <div v-if="product.skus && product.skus.length > 0" class="space-y-6">
             <h3 class="font-bold text-stone-800 text-lg">瑙勬牸閫夋嫨</h3>
             <div class="flex flex-wrap gap-3">
               <button v-for="sku in product.skus" :key="sku.id"
                 @click="selectSku(sku)"
                 :class="selectedSku?.id === sku.id ? 'bg-emerald-600 text-white border-emerald-600' : 'bg-white text-stone-600 border-stone-200 hover:border-emerald-500'"
                 class="px-6 py-3 rounded-2xl border-2 font-bold transition-all text-sm">
                 {{ formatSku(sku) }}
               </button>
             </div>
             <p v-if="selectedSku" class="text-sm text-stone-400">
               库存：<span :class="selectedSku.stock > 10 ? 'text-emerald-600' : 'text-red-500'" class="font-bold">{{ selectedSku.stock > 0 ? selectedSku.stock + ' ' + (product.unit || '件') : '暂时缺货' }}</span>
             </p>
          </div>

          <!-- 鍟嗗搧鍙傛暟 -->
          <div v-if="parsedParams.length > 0" class="bg-white p-6 rounded-3xl border border-stone-100 shadow-sm">
            <h3 class="font-bold text-stone-800 text-lg mb-4 flex items-center">
              <svg class="w-5 h-5 mr-2 text-emerald-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"/></svg>
              鍟嗗搧鍙傛暟
            </h3>
            <div class="divide-y divide-stone-100 rounded-2xl overflow-hidden border border-stone-100">
              <div v-for="(param, idx) in parsedParams" :key="idx" class="flex">
                <div class="w-32 shrink-0 bg-stone-50 px-4 py-3 text-sm font-medium text-stone-500">{{ param.key }}</div>
                <div class="flex-1 px-4 py-3 text-sm text-stone-800">{{ param.value || '-' }}</div>
              </div>
            </div>
          </div>

          <!-- 鍐滄埛淇℃伅 -->
          <div class="bg-white p-6 rounded-3xl border border-stone-100 shadow-sm flex items-center justify-between">
            <div class="flex items-center space-x-4">
              <div class="w-14 h-14 bg-stone-100 rounded-full flex items-center justify-center overflow-hidden border-2 border-emerald-50">
                <FarmerAvatar :src="product.farmerAvatar" :seed="product.farmerName" img-class="w-full h-full object-cover" />
              </div>
              <div>
                <p class="text-xs text-stone-400 font-bold uppercase tracking-widest">提供者</p>
                <h4 class="font-bold text-stone-800">{{ product.farmerName || '绛剧害鍐滄埛' }}</h4>
              </div>
            </div>
            <button @click="goTrace" class="flex items-center text-emerald-600 font-bold text-sm hover:underline">
               鏌ョ湅瀹屾暣婧簮妗ｆ
               <svg class="w-4 h-4 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M9 5l7 7-7 7" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"/></svg>
            </button>
          </div>

          <!-- 璐拱琛屼负 -->
          <div class="pt-8 flex flex-col sm:flex-row gap-4">
            <div class="flex items-center bg-white rounded-2xl border border-stone-200 p-2 sm:w-32 justify-between">
               <button @click="quantity > 1 && quantity--" class="w-8 h-8 flex items-center justify-center text-stone-400 hover:text-emerald-600">-</button>
               <span class="font-bold text-stone-800">{{ quantity }}</span>
               <button @click="quantity++" class="w-8 h-8 flex items-center justify-center text-stone-400 hover:text-emerald-600">+</button>
            </div>
            <button @click="handleAddCart" class="flex-1 bg-white text-emerald-600 border-2 border-emerald-600 py-4 rounded-2xl font-black text-lg hover:bg-emerald-50 transition-all">加入购物车</button>
            <button @click="handleBuyNow" class="flex-1 bg-emerald-600 text-white py-4 rounded-2xl font-black text-lg shadow-lg shadow-emerald-200 hover:bg-emerald-700 hover:-translate-y-1 transition-all">绔嬪嵆璐拱</button>
          </div>

          <!-- 璇︽儏浠嬬粛 -->
          <div class="pt-12 border-t border-stone-200">
             <h3 class="text-2xl font-black text-stone-900 mb-6">浜у搧鏁呬簨</h3>
             <div v-if="hasDetailHtml" class="prose prose-stone max-w-none text-stone-500 leading-relaxed" v-html="product.detailHtml"></div>
             <div v-else class="prose prose-stone max-w-none text-stone-500 leading-relaxed whitespace-pre-line">
               {{ product.description || '鏆傛棤鎻忚堪' }}
             </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 搴曢儴鍥哄畾鏍?(绉诲姩绔? -->
    <div class="lg:hidden fixed bottom-0 left-0 right-0 bg-white/80 backdrop-blur-lg border-t border-stone-200 p-4 flex gap-4 z-50">
       <button @click="goTrace" class="w-14 h-14 bg-emerald-50 text-emerald-600 rounded-2xl flex items-center justify-center">
         <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"/></svg>
       </button>
       <button @click="handleAddCart" class="flex-1 bg-white text-emerald-600 border-2 border-emerald-600 font-black rounded-2xl">加入购物车</button>
       <button @click="handleBuyNow" class="flex-1 bg-emerald-600 text-white font-black rounded-2xl">绔嬪嵆璐拱</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { getProductDetail } from '@/api/frontend/product'
import { addCartItem } from '@/api/frontend/cart'
import { getMemberId, isMemberLoggedIn } from '@/composables/member'
import { useCartStore } from '@/stores/cart'
import FarmerAvatar from '@/components/FarmerAvatar.vue'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const id = route.query.id

const product = ref({})
const selectedSku = ref(null)
const quantity = ref(1)
const currentPreviewPic = ref('')

const albumPicList = computed(() => {
  if (Array.isArray(product.value.albumPicList) && product.value.albumPicList.length > 0) {
    return product.value.albumPicList
      .map(item => String(item || '').trim())
      .filter(Boolean)
  }
  const list = String(product.value.albumPics || '')
    .split(',')
    .map(item => item.trim())
    .filter(Boolean)
  if (list.length > 0) return list
  return product.value.pic ? [product.value.pic] : []
})

const mainDisplayPic = computed(() => {
  return currentPreviewPic.value || product.value.pic || albumPicList.value[0] || ''
})

const hasDetailHtml = computed(() => {
  const html = String(product.value.detailHtml || '').trim()
  if (!html) return false
  const plainText = html
    .replace(/<[^>]*>/g, '')
    .replace(/&nbsp;/gi, '')
    .trim()
  return plainText.length > 0
})

const farmerAvatarSrc = computed(() => {
  return product.value.farmerAvatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${product.value.farmerName || 'farmer'}`
})

// 鍟嗗搧鍙傛暟锛堢洿鎺ヤ粠 API 杩斿洖鐨勬暟缁勮鍙栵級
const parsedParams = computed(() => {
  const params = product.value.productParams || []
  // 杩囨护鎺夌┖鍊肩殑鍙傛暟
  return params.filter(p => p.key && p.key.trim())
})

// 加载商品详情数据
const loadDetail = async () => {
  // 调用 API 获取商品详情，id 来自路由参数
  const res = await getProductDetail(id)
  // 判断接口请求是否成功
  if (res.success) {
    // 将返回的数据赋值给 product 响应式对象
    product.value = res.data
    currentPreviewPic.value = product.value.pic || albumPicList.value[0] || ''
    if (product.value.skus && product.value.skus.length > 0) {
      selectedSku.value = product.value.skus[0]
    }
  }
}

const formatSku = (sku) => {
  if (sku.specs && sku.specs.length > 0) {
    return sku.specs.map(s => s.specValue).join(' / ')
  }
  // 鍏煎鏃?spData JSON 鏍煎紡
  if (!sku.spData) return '榛樿瑙勬牸'
  try {
    const arr = JSON.parse(sku.spData)
    return arr.map(item => item.value).join(' / ')
  } catch (e) {
    return sku.spData
  }
}

const selectSku = (sku) => {
  selectedSku.value = sku
  // SKU 鏈夌嫭绔嬪浘鐗囨椂鍒囨崲涓诲浘
  if (sku.pic) {
    currentPreviewPic.value = sku.pic
  }
}

const goTrace = () => {
  router.push(`/trace/${id}`)
}

// 加入购物车：校验登录和规格，构造数据，调用接口，成功后提示并刷新购物车数量
const handleAddCart = async () => {
  if (!isMemberLoggedIn()) {
    message.warning('璇峰厛鐧诲綍')
    router.push(`/member/login?redirect=${encodeURIComponent(route.fullPath)}`)
    return
  }
  if (!selectedSku.value) {
    message.warning('璇峰厛閫夋嫨鍟嗗搧瑙勬牸')
    return
  }
  try {
    const data = {
      memberId: getMemberId(),
      productId: product.value.id,
      productSkuId: selectedSku.value.id,
      quantity: quantity.value,
      price: selectedSku.value.promotionPrice || selectedSku.value.price || product.value.price,
      productPic: product.value.pic,
      productName: product.value.name,
      productAttr: formatSku(selectedSku.value)
    }
    const res = await addCartItem(data)
    if (res.success) {
      message.success('宸插姞鍏ヨ喘鐗╄溅')
      cartStore.loadCartCount()
    } else {
      message.error(res.message || '加入购物车失败')
    }
  } catch (e) {
    message.error('加入购物车失败')
  }
}

// 立即购买：校验登录和规格，构造订单商品数据，存入 sessionStorage 并跳转结算页
const handleBuyNow = () => {
  if (!isMemberLoggedIn()) {
    message.warning('璇峰厛鐧诲綍')
    router.push(`/member/login?redirect=${encodeURIComponent(route.fullPath)}`)
    return
  }
  if (!selectedSku.value) {
    message.warning('璇峰厛閫夋嫨鍟嗗搧瑙勬牸')
    return
  }

  // 鏋勯€犺鍗曞晢鍝佹暟鎹紙涓嶅寘鍚?cartItemId锛屽洜涓轰笉鏄粠璐墿杞︽潵鐨勶級
  const orderItem = {
    productId: product.value.id,
    productSkuId: selectedSku.value.id,
    productName: product.value.name,
    productPic: product.value.pic,
    productAttr: formatSku(selectedSku.value),
    price: selectedSku.value.promotionPrice || selectedSku.value.price || product.value.price,
    quantity: quantity.value
  }

  // 瀛樺叆 sessionStorage
  sessionStorage.setItem('checkoutItems', JSON.stringify([orderItem]))

  // 璺宠浆鍒扮粨绠楅〉
  router.push('/checkout')
}

onMounted(() => {
  // 组件挂载后自动加载商品详情
  loadDetail()
  // 同步购物车商品数量（用于顶部购物车徽章显示）
  cartStore.loadCartCount()
})
</script>

<style scoped>
/* 鍙互鍦ㄨ繖閲屾坊鍔犱竴浜涢拡瀵硅鎯呭瘜鏂囨湰鐨勬牱寮忓畾鍒?*/
:deep(img) {
  max-width: 100%;
  border-radius: 1.5rem;
  margin: 1rem 0;
}
</style>



