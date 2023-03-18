<template>
    <ContentField v-if="!$store.state.user.pulling">
        <div class="row justify-content-md-center">
            <div class="col-3">
                <div class="mb-3">
                    <form @submit.prevent="login">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input  v-model="username" type="text" class="form-control" id="username" placeholder="请入用户名">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请入密码">
                    </div>
                    <div class="error_message">{{ error_message }}</div>
                    <button type="submit" class="btn btn-success">登录</button>
                    </form>
                </div>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue';
import { useStore } from 'vuex';
import {ref} from 'vue';
import router from '@/router';
export default {
    components: {
        ContentField,
    },
    setup(){
        const store = useStore();
        let username = ref("");
        let password = ref("");
        let error_message = ref("");

        // 取出存在localStorage的token
        const jwt_token=localStorage.getItem("jwt_token"); 
        if (jwt_token){
            store.commit("updataToken",jwt_token);
            store.dispatch("getinfo",{
                success(){
                    router.push({name:"home"});
                    store.commit("updata_pulling", false);
                },
                error(){
                    store.commit("updata_pulling", false);
                }
            })
        }else{
            store.commit("updata_pulling", false);
        }

        const login = ()=>{
            error_message.value = '';
            store.dispatch("login",{
                username:username.value,
                password:password.value,
                success(){
                    store.dispatch("getinfo",{
                        success(){
                            router.push({ name: "home" });
                        },
                        error(){
                            error_message.value = "系统异常";
                        }
                    })
                    
                },
                error(){
                    error_message.value="用户名或密码错误";
                }
            });
        };

        return{
            username,
            password,
            error_message,
            login,
        }
    }
}

</script>

<style scoped>
button{
    width: 100%;
}
.error_message{
    color: red;
}
</style>