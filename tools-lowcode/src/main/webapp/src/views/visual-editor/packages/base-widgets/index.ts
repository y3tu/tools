const modules = import.meta.globEager('./*/index.tsx')

interface componentType{
    [name:string]: any
}
const components : componentType={}

Object.keys(modules).forEach((key: string) => {
    const name = key.replace(/\.\/(.*)\/index\.(tsx|vue)/, '$1')
    components[name] = modules[key]?.default || modules[key]
})

export default components
