import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import { useAuthStore } from '@/store/auth'
import mainLayout from '@/layouts/MainLayout.vue'
import webscoket from '@/components/testmessage.vue'


export const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: mainLayout,
      meta: { requiresAuth: true },
      children: [
          {
            path: '', // 空路径
            redirect: '/chat' 
          },
        
        {
          path: 'home',
          name: 'home',
          component: HomeView,
          meta: { title: '首页' }
          
        },

        {
          path: 'chat',
          component: () => import('@/views/ChatView.vue'),
          meta: { title: '智能助手' }
        },


        {
          path: 'parking',
          meta: { title: '车位管理' },
          children: [


            {
              path: 'spaces',
              component: () => import('@/views/parking/SpacesList.vue'),
              meta: { title: '车位列表' }
            },
            {
              path: 'reservations',
              component: () => import('@/views/parking/Reservations.vue'),
              meta: { title: '我的预约' }
            },
          ]
        },
        {
          path: 'medical',
          component: () => import('@/views/medical/MedicalView.vue'),
          meta: { title: '社区医疗' }
        },
        {
          path: 'activity',
          component: () => import('@/views/activity/ActivityView.vue'),
          meta: { title: '活动申请' }
        },
        {
          path: 'maintenance',
          component: () => import('@/views/ticket/MaintenanceView.vue'),
          meta: {
            title: '工单处理',
            requiresAuth: true,
            requiredRoles: ['MAINTENANCE']
          }
        },
        {
          path: 'ticket',
          component: () => import('@/views/ticket/TicketView.vue'),
          meta: {
            title: '工单服务',
            requiresAuth: true,
            requiredRoles: ['USER']
          }
        }

      ]
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { guestOnly: true }
    },

    {
      path: '/test',
      name: 'test',
      component: webscoket,
      meta: { test: true }
    },

    {
      path: '/admin',
      component: () => import('@/layouts/AdminLayout.vue'),
      meta: { requiresAuth: true, requiredRoles: ['ADMIN', 'SUPER_ADMIN'] },
      children: [
        {
          path: '', // 空路径
          redirect: '/admin/users' // 重定向到 home
        },
        {
          path: 'users',
          component: () => import('@/views/admin/UserManagement.vue'),
          meta: { title: '用户管理' }
        },
        {
          path: 'medical',
          component: () => import('@/views/admin/DoctorManageView.vue'),
          meta: {
            title: '医生管理',
            requiredRoles: ['ADMIN', 'SUPER_ADMIN']
          }
        },
        {
          path: 'parking',
          component: () => import('@/views/admin/ParkingManagement.vue'),
          meta: {
            title: '车位管理',
            requiresAuth: true,
            requiredRoles: ['ADMIN', 'SUPER_ADMIN']
          }
        },

        {
          path: 'rooms',
          children: [
            {
              path: '',
              component: () => import('@/views/admin/RoomManagement.vue'),
              meta: { title: '活动室管理' }
            },
            {
              path: 'activity',
              component: () => import('@/views/admin/BookingApproval.vue'),
              meta: { title: '预约审批' }
            }
          ]
        }
        ,
        {
          path: 'ticket',
          component: () => import('@/views/admin/TicketManagement.vue'),
          meta: {
            title: '车位管理',
            requiresAuth: true,
            requiredRoles: ['ADMIN', 'SUPER_ADMIN']
          }
        },

      ]
    }

  ]
})




router.beforeEach(async (to, from, next) => {
  const store = useAuthStore()


  // 角色验证逻辑
  if (to.meta.requiredRoles) {
    const hasRole = store.user?.role &&
      (to.meta.requiredRoles as string[]).includes(store.user.role)
    if (!hasRole) return next('/home')
  }


  if (to.meta.test) {
    console.log('test')
    next()
    return
  }

  if (to.meta.guestOnly && store.token) {
    next('/home')
    return
  }



  if (to.meta.requiresAuth && !store.token) {
    next('/login')
    return
  }


  if (store.token && !store.user) {
    await store.fetchUser()
  }


  next()
})
