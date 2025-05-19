{{/*
Generate a name for the application
*/}}
{{- define "file-server.name" -}}
{{- printf "%s" .Chart.Name -}}
{{- end -}}

{{/*
Generate the full name of the application, including the release name
*/}}
{{- define "file-server.fullname" -}}
{{- printf "%s-%s" .Release.Name (include "file-server.name" .) | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label
*/}}
{{- define "file-server.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Common labels
*/}}
{{- define "file-server.labels" -}}
helm.sh/chart: {{ include "file-server.chart" . }}
{{ include "file-server.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end -}}

{{/*
Selector labels
*/}}
{{- define "file-server.selectorLabels" -}}
app.kubernetes.io/name: {{ include "file-server.fullname" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}

{{/*
Name of the service account
*/}}
{{- define "file-server.serviceAccountName" -}}
{{ (include "file-server.fullname" .) }}
{{- end -}}
