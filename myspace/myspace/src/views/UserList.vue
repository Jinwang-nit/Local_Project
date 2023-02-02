<template>
    <ConTent>
        <div class="card" v-for="user in users" :key="user.id" @click="open_user_profile(user.id)">
            <div class="card-body">

                <div class="row">
                    <div class="col-1 img-field">
                        <img class="img-fluid" :src="user.photo" alt="">
                    </div>

                    <div class="col-11">
                        <div class="username">{{ user.username }}</div>
                        <div class="follower-count">{{ user.followerCount }}</div>
                    </div>
                </div>

            </div>
        </div>
    </ConTent>
</template>

<script>
import ConTent from '../components/ConTent'
import $ from 'jquery';
import {ref} from 'vue';
import router from '@/router';
import {useStore} from 'vuex';
export default {
    name: 'UserList',
    components: {
        ConTent,
    },
    setup(){
        let users=ref([]);
        // ajax一个前后台配合的技术，它可以让javascript发送http请求，与后台通信，获取数据和信息。jquery将它封装成了一个函数$.ajax()
        // https://blog.csdn.net/wumingid/article/details/90415654
        $.ajax({
            url:'https://app165.acapp.acwing.com.cn/myspace/userlist/',
            type:"get",
            // success 设置请求成功后的回调函数
            success(resp){
                users.value=resp;
            }
        });

        const store = useStore();
        const open_user_profile = userId =>{
            if (store.state.user.is_login){
                router.push({
                    name:"userprofile",
                    params:{
                        userId
                    }
                })
            }else{
                router.push({
                    name:"login"
                });
            }
        }

        return{
            users,
            open_user_profile,
        }
    }
}
</script>

<style scoped>
img{
    border-radius: 50%;
}
.username{
    font-weight: bold;
    height: 50%;
}
.follower-count{
    height: 50%;
}
.card{
    margin-top: 10px;
    cursor: pointer;
}
.card:hover{
    box-shadow: 2px 2px 10px lightslategray;
    transition: 500ms;
}
.img-field{
    display: flex;
    flex-direction: column;
    justify-content: center;
}
</style>
