<template>
  <div class="me-view-comment-item">
    <div class="me-view-comment-author">
      <div class="me-view-info">
        <i class="icon-user"></i>
        <span class="me-view-nickname">{{comment.nickname}}</span>
      </div>
    </div>
    <div>
      <p class="me-view-comment-content">{{comment.content}}</p>
      <div class="me-view-meta">
        <span style="color: #335A66">{{index + 1}}楼  {{comment.publishDate}}</span>
      </div>
      <div class="comment-edit-icon-class">
        <a @click="showComment(-1,comment.nickname,comment.uid)">
          <i class="icon-comment"></i>
        </a>
        <a v-show="deleteShow" @click="deleteMyComment()" style="margin-left: 15px">
          <i class="icon-delete"></i>
        </a>
        <a v-show="updateShow" @click="updateMyComment()" style="margin-left: 15px">
          <i class="icon-edit"></i>
        </a>
      </div>

      <div class="me-reply-list">
        <div class="me-reply-item" v-for="c in comment.childrens" :key="c.id">
          <div style="font-size: 14px">
            <span class="me-reply-user">{{c.nickname}}:&nbsp;&nbsp;</span>
            <span v-if="c.level == 2" class="me-reply-user">@{{c.toUserNickname}} </span>
            <span>{{c.content}}</span>
          </div>
          <div class="me-view-meta" style="margin-top: 10px;color: #78b6f7">
            <span style="padding-right: 10px">{{c.publishDate}}</span>
          </div>
          <div class="comment-edit-icon-class2">
            <a @click="deleteMyComment2(c.id)">
              <i class="icon-small-delete"></i>
            </a>
            <a @click="updateMyComment2(c.id)">
              <i class="icon-small-edit"></i>
            </a>
          </div>
        </div>
        <div class="me-view-comment-write" v-show="commentShow">
          <el-input
              v-model="reply.content"
              type="input"
              style="width: 90%"
              :placeholder="placeholder"
              class="me-view-comment-text"
              resize="none">
          </el-input>
          <el-button style="margin-left: 8px" @click="publishChildComment()" type="text">评论</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {deleteRequest, getRequest, postRequest, putRequest} from '../utils/api'

export default {
  name: "CommentItem",
  props: {
    articleId: Number,
    comment: Object,
    index: Number,
    rootCommentCounts: Number,

  },
  mounted: function (){
    let _this = this;
    this.getCurrentUid();
    // if(this.comment.uid != this.currentUid){
    //   _this.deleteShow = false;
    // }
  },
  data() {
    return {
      currentUid: '',
      placeholder: '你的评论...',
      commentShow: false,
      deleteShow: true,
      updateShow: true,
      commentShowIndex: '',
      reply: this.getEmptyReply(),
      id:'',
      content:''
    }
  },
  methods: {
    showComment(commentShowIndex, toUserNickname, toUid) {
      let that = this
      that.reply = that.getEmptyReply()

      if (this.commentShowIndex !== commentShowIndex) {
        if (toUserNickname) {
          that.placeholder = `@${toUserNickname} `
          that.reply.toUid = toUid
        } else {
          that.placeholder = '你的评论...'
        }
        that.commentShow = true
        that.commentShowIndex = commentShowIndex
      } else {
        that.commentShow = false
        that.commentShowIndex = ''
      }
    },
    publishChildComment() {
      let that = this
      /*if (!that.reply.content) {
        return;
      }*/
      postRequest('/comment/', that.reply).then(data => {
        if(data.status == 200){
          if(data.data.status == 'success'){
            that.$message.success('评论成功!')
            if(!that.comment.childrens){
              that.comment.childrens = []
            }
            that.comment.childrens.unshift(data.data)
            that.$emit('commentCountsIncrement')
            that.showComment(that.commentShowIndex)

          }else{
            that.$message.error(data.data.msg);
          }
        }else{
          that.$message.error(data.data.msg)
        }

      }).catch(error => {
        if (error !== 'error') {
          that.$message({type: 'error', message: '评论失败', showClose: true})
        }
      })
    },
    deleteMyComment(){
      let _this = this;
      this.$confirm('确认删除这条评论吗', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        _this.deleteComment(this.comment.id);
      }).catch(() => {
        //取消
        _this.loading = false;
      });
    },
    deleteComment(id){
      var _this = this;
      this.loading = true;
      //删除
      deleteRequest("/comment/" + id).then(resp=> {
        var json = resp.data;
        _this.$message({
          type: json.status,
          message: json.msg
        });
      }, resp=> {
        _this.loading = false;
        if (resp.response.status == 403) {
          _this.$message({
            type: 'error',
            message: resp.response.data
          });
        } else if (resp.response.status == 500) {
          _this.$message({
            type: 'error',
            message: '该评论下尚有评论，删除失败!'
          });
        }
      })
    },
    updateMyComment(){
      var _this = this;
      this.$prompt('请输入新评论', '编辑', {
        confirmButtonText: '更新',
        inputValue: '',
        cancelButtonText: '取消'
      }).then(({value}) => {
        //value就是输入值
        if (value == null || value.length == 0) {
          _this.$message({
            type: 'info',
            message: '评论不能为空!'
          });
        } else {
          _this.loading = true;
          putRequest("/comment/", {
              id: this.comment.id,
              content: value
          }).then(resp=> {
            var json = resp.data;
            _this.$message({
              type: json.status,
              message: json.msg
            });
            _this.refresh();
          }, resp=> {
            if (resp.response.status == 403) {
              _this.$message({
                type: 'error',
                message: resp.response.data
              });
            }
            _this.loading = false;
          });
        }
      });
    },
    deleteMyComment2(id){
      let _this = this;
      this.$confirm('确认删除这条评论吗', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        _this.deleteComment(id);
      }).catch(() => {
        //取消
        _this.loading = false;
      });
    },
    updateMyComment2(id){
      var _this = this;
      this.$prompt('请输入新评论', '编辑', {
        confirmButtonText: '更新',
        inputValue: '',
        cancelButtonText: '取消'
      }).then(({value}) => {
        //value就是输入值
        if (value == null || value.length == 0) {
          _this.$message({
            type: 'info',
            message: '评论不能为空!'
          });
        } else {
          _this.loading = true;
          putRequest("/comment/", {
            id: id,
            content: value
          }).then(resp=> {
            var json = resp.data;
            _this.$message({
              type: json.status,
              message: json.msg
            });
            _this.refresh();
          }, resp=> {
            if (resp.response.status == 403) {
              _this.$message({
                type: 'error',
                message: resp.response.data
              });
            }
            _this.loading = false;
          });
        }
      });
    },

    getCurrentUid() {
      var _this = this;
      getRequest("/currentUserId").then(resp=> {
        if (resp.status == 200) {
          _this.currentUid = resp.data;
        }
      })
    },
    getEmptyReply() {
      return {
        articleId: this.articleId,
        parentId: this.comment.id,
        //toUserId: '',
        content: '',
        toUid: '',
      }
    }
  }
}
</script>

<style>
.icon-small-delete:hover {
  color: #78b6f7;
}

.icon-small-edit:hover {
  color: #78b6f7;
}

.icon-edit:hover {
  color: #78b6f7;
}
.icon-delete:hover {
  color: #78b6f7;
}
.icon-comment:hover {
  color: #78b6f7;
}

.comment-edit-icon-class {
  margin-top: 10px;
  margin-left: 800px;
  height: 60px;
  color: #335A66;
}

.comment-edit-icon-class {
  margin-top: 10px;
  height: 60px;
  color: #335A66;
}

.me-view-meta {
  font-size: 10px;
}

.me-view-comment-write {
  margin-top: 20px;
}

.me-view-comment-text {
  font-size: 16px;
}

.v-show-content {
  padding: 8px 25px 15px 30px !important;
}

.v-note-wrapper .v-note-panel {
  box-shadow: none !important;
}

.me-view-comment-item {
  margin-top: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.me-view-comment-author {
  margin: 10px 0;
  vertical-align: middle;
}

.me-view-nickname {
  font-size: 25px;
  font-family: "AR PL UKai CN";
  color: #335A66;
}

.me-view-comment-content {
  line-height: 1.5;
  font-family: Avenir;
}

.me-view-comment-tools {
  margin-top: 4px;
  margin-bottom: 10px;
}

.me-view-comment-tool {
  font-size: 13px;
  color: #a6a6a6;
  padding-right: 14px;
}

.v-note-wrapper .v-note-panel .v-note-show .v-show-content, .v-note-wrapper .v-note-panel .v-note-show .v-show-content-html {
  background: #fff !important;
}

.me-reply-list {
  padding-left: 16px;
  border-left: 4px solid #c5cac3;
}

.me-reply-item {
  margin-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.me-reply-user {
  color: #78b6f7;
}
</style>
