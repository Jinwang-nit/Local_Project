<template>
    <ConTent>
        <div class="row">
            <div class="col-3">
                <UserProfileinfo @follow="follow" @unfollow="unfollow" :user="user"></UserProfileinfo> <!-- 父类向子类里传递user对象-->
                <UserProfilewrite v-if="is_me"  @post_a_post="post_a_post"></UserProfilewrite>
            </div>
            <div class="col-9">
                <p>好 友 动 态</p>
                <UserProfilepost :user="user" :posts="posts" @delete_a_post="delete_a_post"></UserProfilepost>
            </div>
        </div>
    </ConTent>
</template>

<script>
import ConTent from '../components/ConTent';
import UserProfileinfo from '../components/UserProfileinfo';
import UserProfilepost from '../components/UserProfilepost' ;
import UserProfilewrite from '../components/UserProfilewrite';
import { reactive } from 'vue';
import {useRoute} from 'vue-router';
import $ from 'jquery';
import {useStore} from 'vuex';
import {computed} from 'vue';
export default {
    name: 'UserProfile',
    components: {
        ConTent,
        UserProfileinfo,
        UserProfilepost,
        UserProfilewrite,
},
    setup(){ // 可以把函数里的变量函数抛出
         const route = useRoute(); // 访问路由里的参数，route.params.参数名访问
         const userId=parseInt(route.params.userId);

        const user = reactive({});
        const posts = reactive({}); // reactive变量会自动更新
        
        const store = useStore();
        // 获取个人信息
        $.ajax({
            url:"https://app165.acapp.acwing.com.cn/myspace/getinfo/",
            type:"GET",
            data:{
                user_id:userId
            },
            headers:{
                'Authorization': "Bearer " + store.state.user.access
            },
            success(resp){
                user.id=resp.id;
                user.username=resp.username;
                user.photo=resp.photo;
                user.followerCount = resp.followerCount;
                user.is_follower = resp.is_followed;

            }
        });
        // 获取所有帖子
        $.ajax({
            url:"https://app165.acapp.acwing.com.cn/myspace/post/",
            type:"GET",
            data:{
                user_id:userId
            },
            headers:{
                'Authorization': "Bearer " + store.state.user.access
            },
            success(resp){
                posts.count = resp.length;
                posts.posts=resp;
            }
        })

        const post_a_post=(content)=>{
            posts.count++;
            posts.posts.unshift({
                id: posts.count,
                userId: 1,
                content:content,
            });
        }

        const delete_a_post=(post_id)=>{
            posts.posts = posts.posts.filter(post => post.id !== post_id);
            posts.count=posts.posts.length;
        }

        const follow = () =>{
            if (user.is_follower) return;
            user.is_follower=true;
            user.followerCount++;
        }

        const unfollow=()=>{
            if (!user.is_follower) return;
            user.is_follower=false;
            user.followerCount=Math.max(0,user.followerCount-1);
        }

        const is_me=computed(()=>{
            if (userId === store.state.user.id)
                return true;
            else
                return false;
        })

        return { // 需要抛出的return
            user,
            follow,
            unfollow,
            post_a_post,
            delete_a_post,
            posts,
            is_me,
        }
    }
}
</script>

<style scoped>
p{
    font-size: 20px;
    font-weight: bold;
}
</style>
