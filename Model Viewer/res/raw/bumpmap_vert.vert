uniform mat4 matViewProjectionInverseTranspose;
uniform mat4 matViewProjection;
uniform vec3 fvLightPosition;
uniform vec3 fvEyePosition;

varying vec2 Texcoord;
varying vec3 ViewDirection;
varying vec3 LightDirection;

attribute vec4 rm_Vertex;
attribute vec4 rm_TexCoord0;
attribute vec4 rm_Normal;
attribute vec4 rm_Binormal;
attribute vec4 rm_Tangent;
   
void main( void )
{
   gl_Position = matViewProjection * rm_Vertex;
   Texcoord    = rm_TexCoord0.xy;

   vec4 fvObjectPosition = matViewProjection * rm_Vertex;

   vec3 fvViewDirection  = fvEyePosition - fvObjectPosition.xyz;
   vec3 fvLightDirection = fvLightPosition - fvObjectPosition.xyz;
 
 vec3 tangent; 
vec3 binormal; 

vec3 c1 = cross( rm_Normal.xyz, vec3(0.0, 0.0, 1.0) ); 
vec3 c2 = cross( rm_Normal.xyz, vec3(0.0, 1.0, 0.0) ); 
if( length(c1)>length(c2) )
{
tangent = c1;   
}
else
{
tangent = c2;   
}

tangent = normalize(tangent);

binormal = cross(rm_Normal.xyz, tangent); 
binormal = normalize(binormal);
vec4 fullbinormal;
fullbinormal.xyz = binormal;
vec4 fulltangent;
fulltangent.xyz = tangent;

   vec3 fvNormal         = (matViewProjectionInverseTranspose * rm_Normal).xyz;
   vec3 fvBinormal       = (matViewProjectionInverseTranspose * fullbinormal).xyz;
   vec3 fvTangent        = (matViewProjectionInverseTranspose * fulltangent).xyz;
 
   ViewDirection.x  = dot( fvTangent, fvViewDirection );
   ViewDirection.y  = dot( fvBinormal, fvViewDirection );
   ViewDirection.z  = dot( fvNormal, fvViewDirection );

   LightDirection.x  = dot( fvTangent, fvLightDirection.xyz );
   LightDirection.y  = dot( fvBinormal, fvLightDirection.xyz );
   LightDirection.z  = dot( fvNormal, fvLightDirection.xyz );
   
}
