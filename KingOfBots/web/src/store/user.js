
import $ from 'jquery';
const ModuleUser ={
  state: {
    id:"",
    username:"",
    photo:"",
    is_login:false,
    token:"",
    pulling:true,
  },
  getters: {
  },
  mutations: { // commit()
    updataUser(state,user){
        state.id=user.id;
        state.username=user.username;
        state.photo = user.photo;
        state.is_login = user.is_login;
    },
    updataToken(state,token){
        state.token = token;
    },
    logout(state){
        state.id="";
        state.username="";
        state.is_login=false;
        state.photo="";
        state.token="";

    },
    updata_pulling(state,pulling){
      state.pulling = pulling;
    }
  },
  actions: { // dispatch()
    login(context,data){
        $.ajax({
            url:"http://127.0.0.1:3000/user/account/token/",
            type:"POST",
            data:{
                username:data.username,
                password:data.password,
            },
            success(resp){
                if (resp.error_message === "success"){
                    localStorage.setItem("jwt_token",resp.token);
                    context.commit("updataToken",resp.token);
                    data.success(resp);
                }else{
                  data.error(resp);
                }
            },
            error(resp){
              data.error(resp);
            }
    });
  },
  getinfo(context,data){
    $.ajax({
      url:"http://127.0.0.1:3000/user/account/info/",
      type:"GET",
      headers:{
        // 授权
        Authorization:"Bearer "+ context.state.token,
      },
      success(resp){
        if (resp.error_message === "success"){
            context.commit("updataUser",{
            ...resp,
            is_login: true,
          });
          data.success();
        }else{
          data.error();
        }
      },
      error(){
        data.error();
      }
    });
  },

  logout(context){
    localStorage.removeItem("jwt_token");
    context.commit("logout");
  },

  },
  modules: {
  }
}

export default ModuleUser;
