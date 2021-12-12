import {ElMessage} from 'element-plus'

let toast=  {
    info(msg:string = '', duration:number):void{
        ElMessage({
            message: msg,
            type:'info',
            duration:duration
        })
    },
    error(msg:string = '', duration:number):void{
        ElMessage({
            message: msg,
            type:'error',
            duration:duration
        })
    },
    success(msg:string = '', duration:number):void{
        ElMessage({
            message: msg,
            type:'success',
            duration:duration
        })
    },

    warning(msg:string = '', duration:number):void{
        ElMessage({
            message: msg,
            type:'warning',
            duration:duration
        })
    }

}

export default toast;