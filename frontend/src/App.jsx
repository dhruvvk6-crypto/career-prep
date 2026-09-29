import { useEffect, useMemo, useState } from 'react';
import { api, ApiError } from './api';

const EMPTY_DASHBOARD = {
  interviewScore: 0,
  interviewsCompleted: 0,
  questionsAttempted: 0,
  javaProgress: 0,
  dsaProgress: 0,
  sqlProgress: 0
};

export default function App() {
  const [token, setToken] = useState(() => localStorage.getItem('careerprep_token'));
  const [page, setPage] = useState('dashboard');
  const [dashboard, setDashboard] = useState(EMPTY_DASHBOARD);
  const [result, setResult] = useState(null);
  const [learningPaths, setLearningPaths] = useState([]);
  const [insights, setInsights] = useState(null);
  const [toast, setToast] = useState('');

  const loadWorkspace = async () => {
    const [dashboardData, pathsData, insightsData] = await Promise.all([
      api('/api/dashboard'), api('/api/learning-paths'), api('/api/practice/insights')
    ]);
    setDashboard(dashboardData);
    setLearningPaths(pathsData);
    setInsights(insightsData);
  };

  useEffect(() => {
    if (!token) return;
    loadWorkspace().catch((error) => {
      if (error.status === 401) signOut();
      else setToast(error.message);
    });
  }, [token]);

  const signOut = () => {
    localStorage.removeItem('careerprep_token');
    setToken(null);
    setPage('dashboard');
    setResult(null);
  };

  const showToast = (message) => {
    setToast(message);
    window.setTimeout(() => setToast(''), 3500);
  };

  if (!token) {
    return <AuthScreen onAuthenticated={(newToken) => {
      localStorage.setItem('careerprep_token', newToken);
      setToken(newToken);
    }} />;
  }

  return (
    <div className="app-shell">
      <Sidebar page={page} setPage={setPage} signOut={signOut} />
      <main className="content">
        {toast && <div className="toast" role="status">{toast}</div>}
        {page === 'dashboard' && <Dashboard dashboard={dashboard} insights={insights} onNavigate={setPage} />}
        {page === 'paths' && <LearningPaths paths={learningPaths} onPractice={(targetRole) => {
          setPage('interview');
          sessionStorage.setItem('careerprep_selected_role', targetRole);
        }} />}
        {page === 'insights' && <ProgressInsights insights={insights} onPractice={() => setPage('practice')} />}
        {page === 'practice' && <PracticeLabs learningPaths={learningPaths} refresh={loadWorkspace} notify={showToast} />}
        {page === 'interview' && <Interview onResult={(nextResult) => { setResult(nextResult); setPage('results'); }} />}
        {page === 'results' && <Results result={result} onStartInterview={() => setPage('interview')} />}
      </main>
    </div>
  );
}

function AuthScreen({ onAuthenticated }) {
  const [mode, setMode] = useState('login');
  const [form, setForm] = useState({ name: '', email: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const submit = async (event) => {
    event.preventDefault();
    setError('');
    setLoading(true);
    try {
      if (mode === 'register') {
        await api('/api/auth/register', { method: 'POST', body: JSON.stringify(form) });
      }
      const login = await api('/api/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email: form.email, password: form.password })
      });
      onAuthenticated(login.token);
    } catch (caughtError) {
      const fieldMessage = Object.values(caughtError.fieldErrors ?? {})[0];
      setError(fieldMessage ?? caughtError.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-layout">
      <section className="auth-intro">
        <span className="eyebrow">CAREERPREP</span>
        <h1>Practice with purpose.<br />Grow with proof.</h1>
        <p>Build skills, run focused mock interviews, and turn feedback into measurable progress.</p>
        <div className="intro-points"><span>✓ Role-specific prompts</span><span>✓ Clear feedback</span><span>✓ Progress tracking</span></div>
      </section>
      <section className="auth-card-wrap">
        <form className="auth-card" onSubmit={submit}>
          <span className="brand-mark">CP</span>
          <h2>{mode === 'login' ? 'Welcome back' : 'Create your account'}</h2>
          <p>{mode === 'login' ? 'Sign in to continue your preparation.' : 'Start building your interview confidence.'}</p>
          {mode === 'register' && <Field label="Name" value={form.name} onChange={(name) => setForm({ ...form, name })} placeholder="Your name" />}
          <Field label="Email" type="email" value={form.email} onChange={(email) => setForm({ ...form, email })} placeholder="you@example.com" />
          <Field label="Password" type="password" value={form.password} onChange={(password) => setForm({ ...form, password })} placeholder="At least 8 characters" />
          {error && <p className="form-error">{error}</p>}
          <button className="primary-button full" disabled={loading}>{loading ? 'Please wait…' : mode === 'login' ? 'Sign in' : 'Create account'}</button>
          <p className="switch-copy">{mode === 'login' ? 'New to CareerPrep?' : 'Already have an account?'} <button type="button" className="link-button" onClick={() => { setMode(mode === 'login' ? 'register' : 'login'); setError(''); }}>{mode === 'login' ? 'Create one' : 'Sign in'}</button></p>
        </form>
      </section>
    </div>
  );
}

function Field({ label, value, onChange, ...inputProps }) {
  return <label className="field"><span>{label}</span><input value={value} onChange={(event) => onChange(event.target.value)} required {...inputProps} /></label>;
}

function Sidebar({ page, setPage, signOut }) {
  const nav = [['dashboard', '▦', 'Dashboard'], ['paths', '▤', 'Learning paths'], ['insights', '◌', 'Progress'], ['practice', '⌘', 'Practice labs'], ['interview', '◉', 'Mock interview'], ['results', '◇', 'Results']];
  return <aside className="sidebar">
    <div className="logo"><span>CP</span><strong>CareerPrep</strong></div>
    <nav>{nav.map(([id, icon, label]) => <button key={id} className={page === id ? 'nav-item active' : 'nav-item'} onClick={() => setPage(id)}><span>{icon}</span>{label}</button>)}</nav>
    <button className="nav-item signout" onClick={signOut}><span>↗</span>Sign out</button>
  </aside>;
}

function LearningPaths({ paths, onPractice }) {
  return <section className="page"><PageHeader eyebrow="LEARNING PATHS" title="Choose your developer track." subtitle="Each path links focused topics to mock interviews at your level." />
    <div className="path-grid">{paths.length === 0 ? <EmptyState title="Learning paths are loading" body="Refresh once the backend has started and seeded its question bank." /> : paths.map((path) => <article className="path-card" key={path.id} style={{ '--path-color': path.accentColor }}><div className="path-top"><span>{path.technology}</span><i /></div><h2>{path.name}</h2><p>{path.description}</p><div className="topic-list">{path.topics.map((topic, index) => <div key={topic.name}><strong>{String(index + 1).padStart(2, '0')}</strong><span><b>{topic.name}</b><small>{topic.description}</small></span></div>)}</div><button className="primary-button" onClick={() => onPractice(path.targetRole)}>Practice this path →</button></article>)}</div>
  </section>;
}

function Dashboard({ dashboard, insights, onNavigate }) {
  const completedLabs = insights?.pathReadiness.reduce((total, path) => total + path.completed, 0) ?? 0;
  const metrics = [
    ['Interview score', `${Math.round(dashboard.interviewScore ?? 0)}%`, 'Your average result'],
    ['Interviews done', dashboard.interviewsCompleted ?? 0, 'Completed sessions'],
    ['Labs completed', completedLabs, 'Hands-on practice saved']
  ];
  const focus = insights?.topicProgress.slice(0, 3).map((topic) => ({
    id: `${topic.path}-${topic.topic}`, name: topic.topic, progress: topic.progress
  })) ?? [];
  return <section className="page"><PageHeader eyebrow="YOUR WORKSPACE" title="Good to see you." subtitle="A little deliberate practice every day compounds quickly." />
    <div className="metric-grid">{metrics.map(([label, value, detail]) => <article className="metric-card" key={label}><span>{label}</span><strong>{value}</strong><small>{detail}</small></article>)}</div>
    <div className="two-column"><article className="panel progress-panel"><div className="panel-heading"><div><span className="eyebrow">VERIFIED PROGRESS</span><h2>Current focus</h2></div><button className="text-action" onClick={() => onNavigate('insights')}>View details →</button></div>{focus.length ? focus.map((skill) => <ProgressRow key={skill.id} skill={skill} />) : <p className="empty-copy">Complete a practice lab to begin measuring your real progress.</p>}</article>
      <article className="panel action-panel"><span className="eyebrow">READY WHEN YOU ARE</span><h2>Run a mock interview</h2><p>Choose a role and difficulty, then work through targeted questions against the clock.</p><button className="primary-button" onClick={() => onNavigate('interview')}>Start an interview <span>→</span></button></article></div>
    {insights && <section className="readiness-section"><div className="panel-heading"><div><span className="eyebrow">LEARNING ANALYTICS</span><h2>Path readiness</h2></div><button className="text-action" onClick={() => onNavigate('practice')}>Open practice labs →</button></div><p className="recommendation">✦ {insights.recommendedNextPractice}</p><div className="readiness-grid">{insights.pathReadiness.map((path) => <article className="readiness-card" key={path.path}><div><strong>{path.name}</strong><span>{path.completed}/{path.total} labs completed</span></div><b>{Math.round(path.readiness)}%</b><div className="progress-track"><div style={{ width: `${path.readiness}%` }} /></div></article>)}</div></section>}
  </section>;
}

function ProgressRow({ skill }) {
  const progress = Number(skill.progress ?? 0);
  return <div className="progress-row"><div className="progress-label"><span>{skill.name}</span><strong>{Math.round(progress)}%</strong></div><div className="progress-track"><div style={{ width: `${progress}%` }} /></div></div>;
}

function ProgressInsights({ insights, onPractice }) {
  if (!insights) return <section className="page"><PageHeader eyebrow="PROGRESS" title="Loading your evidence." subtitle="Your practice and interview activity will appear here." /></section>;
  return <section className="page"><PageHeader eyebrow="PROGRESS & INSIGHTS" title="Progress you can prove." subtitle="Readiness is calculated from completed labs and their rubric scores—not manual sliders." />
    <section className="recommendation-card"><span className="eyebrow">RECOMMENDED NEXT</span><h2>{insights.recommendedNextPractice}</h2><button className="primary-button" onClick={onPractice}>Open practice labs →</button></section>
    <section className="insight-section"><span className="eyebrow">LEARNING-PATH READINESS</span><div className="readiness-grid large">{insights.pathReadiness.map((path) => <article className="readiness-card" key={path.path}><div><strong>{path.name}</strong><span>{path.completed}/{path.total} labs completed</span></div><b>{Math.round(path.readiness)}%</b><div className="progress-track"><div style={{ width: `${path.readiness}%` }} /></div></article>)}</div></section>
    <section className="insight-section"><span className="eyebrow">TOPIC BREAKDOWN</span><div className="topic-table">{insights.topicProgress.map((topic) => <article key={`${topic.path}-${topic.topic}`}><div><b>{topic.topic}</b><small>{topic.path}</small></div><span>{topic.completed}/{topic.total} completed</span><strong>{Math.round(topic.progress)}%</strong><div className="progress-track"><div style={{ width: `${topic.progress}%` }} /></div><em>{topic.completed ? `${Math.round(topic.averageScore)}% average score` : 'Not started'}</em></article>)}</div></section>
  </section>;
}

function PracticeLabs({ learningPaths, refresh, notify }) {
  const [selectedPath, setSelectedPath] = useState('');
  const [challenges, setChallenges] = useState([]);
  const [selectedId, setSelectedId] = useState(null);
  const [draft, setDraft] = useState('');
  const [saving, setSaving] = useState(false);

  const loadChallenges = async (path = selectedPath) => {
    try {
      const response = await api(`/api/practice/challenges${path ? `?path=${encodeURIComponent(path)}` : ''}`);
      setChallenges(response);
      const current = response.find((challenge) => challenge.id === selectedId) ?? response[0];
      setSelectedId(current?.id ?? null);
      setDraft(current?.answer ?? current?.starterCode ?? '');
    } catch (error) { notify(error.message); }
  };
  useEffect(() => { loadChallenges(); }, [selectedPath]);
  const selected = useMemo(() => challenges.find((challenge) => challenge.id === selectedId), [challenges, selectedId]);
  const choose = (challenge) => { setSelectedId(challenge.id); setDraft(challenge.answer || challenge.starterCode || ''); };
  const save = async (completed) => {
    if (!selected) return;
    setSaving(true);
    try {
      const result = await api(`/api/practice/challenges/${selected.id}/attempt`, { method: 'PUT', body: JSON.stringify({ answer: draft, completed }) });
      setChallenges(challenges.map((challenge) => challenge.id === selected.id ? { ...challenge, answer: draft, completed: result.status === 'COMPLETED', score: result.score } : challenge));
      notify(completed ? `Challenge completed — ${Math.round(result.score)}% rubric score.` : 'Draft saved.');
      await refresh();
    } catch (error) { notify(error.message); }
    finally { setSaving(false); }
  };

  return <section className="page"><PageHeader eyebrow="PRACTICE LABS" title="Build, explain, improve." subtitle="Save work as a draft or complete a challenge for topic-level progress." />
    <div className="practice-filter"><button className={selectedPath === '' ? 'filter active' : 'filter'} onClick={() => setSelectedPath('')}>All paths</button>{learningPaths.map((path) => <button className={selectedPath === path.targetRole ? 'filter active' : 'filter'} onClick={() => setSelectedPath(path.targetRole)} key={path.id}>{path.technology}</button>)}</div>
    <div className="lab-layout"><aside className="lab-list">{challenges.map((challenge) => <button className={selected?.id === challenge.id ? 'lab-item active' : 'lab-item'} key={challenge.id} onClick={() => choose(challenge)}><span className={challenge.completed ? 'lab-status complete' : 'lab-status'}>{challenge.completed ? '✓' : '○'}</span><div><b>{challenge.title}</b><small>{challenge.language} · {challenge.topic}</small></div>{challenge.score != null && <em>{Math.round(challenge.score)}%</em>}</button>)}</aside>{selected ? <article className="lab-card"><div className="lab-meta">{[selected.language, selected.challengeType, selected.topic].filter((tag, index, tags) => tags.indexOf(tag) === index).map((tag) => <span key={tag}>{tag}</span>)}</div><h2>{selected.title}</h2><p>{selected.prompt}</p><textarea className="code-editor" value={draft} onChange={(event) => setDraft(event.target.value)} spellCheck="false" aria-label="Practice answer or code" /><div className="lab-actions"><small>{draft.length}/10000 characters</small><div><button className="secondary-button" disabled={saving} onClick={() => save(false)}>Save draft</button><button className="primary-button" disabled={saving} onClick={() => save(true)}>{saving ? 'Saving…' : 'Complete challenge'}</button></div></div></article> : <EmptyState title="No challenges found" body="Choose another learning path." />}</div>
  </section>;
}

function Interview({ onResult }) {
  const [configuration, setConfiguration] = useState(null);
  const [setup, setSetup] = useState({ targetRole: sessionStorage.getItem('careerprep_selected_role') ?? 'Java Backend Developer', difficulty: 'BEGINNER' });
  const [interview, setInterview] = useState(null);
  const [answers, setAnswers] = useState({});
  const [activeIndex, setActiveIndex] = useState(0);
  const [secondsLeft, setSecondsLeft] = useState(20 * 60);
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  useEffect(() => { api('/api/interviews/configuration').then((data) => {
    setConfiguration(data);
    if (!data.roles.includes(setup.targetRole)) setSetup((current) => ({ ...current, targetRole: data.roles[0] }));
  }).catch((caughtError) => setError(caughtError.message)); }, []);
  useEffect(() => {
    if (!interview || secondsLeft <= 0) return;
    const timer = window.setInterval(() => setSecondsLeft((seconds) => seconds - 1), 1000);
    return () => window.clearInterval(timer);
  }, [interview, secondsLeft]);

  const start = async (event) => {
    event.preventDefault(); setError(''); setSaving(true);
    try { const created = await api('/api/interviews', { method: 'POST', body: JSON.stringify(setup) }); setInterview(created); setAnswers(Object.fromEntries(created.questions.map((question) => [question.id, { text: '', answerId: null }]))); setSecondsLeft(20 * 60); }
    catch (caughtError) { setError(caughtError.message); }
    finally { setSaving(false); }
  };
  const saveAnswer = async (questionId) => {
    const draft = answers[questionId];
    if (!draft.text.trim()) throw new Error('Write an answer before saving.');
    const saved = draft.answerId
      ? await api(`/api/interviews/${interview.id}/answers/${draft.answerId}`, { method: 'PUT', body: JSON.stringify({ answer: draft.text }) })
      : await api(`/api/interviews/${interview.id}/answers`, { method: 'POST', body: JSON.stringify({ questionId, answer: draft.text }) });
    setAnswers((current) => ({ ...current, [questionId]: { text: saved.answer, answerId: saved.id } }));
  };
  const complete = async () => {
    setError(''); setSaving(true);
    try {
      for (const question of interview.questions) if (!answers[question.id]?.answerId) await saveAnswer(question.id);
      await api(`/api/interviews/${interview.id}/complete`, { method: 'PUT' });
      onResult(await api(`/api/interviews/${interview.id}/result`));
    } catch (caughtError) { setError(caughtError.message); }
    finally { setSaving(false); }
  };

  if (!interview) return <section className="page"><PageHeader eyebrow="MOCK INTERVIEW" title="Practice under pressure." subtitle="Choose a focus. We will bring the questions." />
    <form className="setup-card" onSubmit={start}><label className="field"><span>Target role</span><select value={setup.targetRole} onChange={(event) => setSetup({ ...setup, targetRole: event.target.value })}>{(configuration?.roles ?? []).map((role) => <option key={role}>{role}</option>)}</select></label><label className="field"><span>Difficulty</span><select value={setup.difficulty} onChange={(event) => setSetup({ ...setup, difficulty: event.target.value })}>{(configuration?.difficulties ?? []).map((difficulty) => <option key={difficulty}>{difficulty}</option>)}</select></label>{error && <p className="form-error">{error}</p>}<button className="primary-button" disabled={saving || !configuration}>{saving ? 'Creating…' : 'Begin interview →'}</button></form>
  </section>;

  const question = interview.questions[activeIndex];
  const minutes = String(Math.floor(secondsLeft / 60)).padStart(2, '0');
  const seconds = String(secondsLeft % 60).padStart(2, '0');
  return <section className="page interview-page"><div className="interview-top"><div><span className="eyebrow">{interview.targetRole} · {interview.difficulty}</span><h1>Mock interview</h1></div><div className={secondsLeft < 120 ? 'timer urgent' : 'timer'}>◷ {minutes}:{seconds}</div></div>
    <div className="interview-layout"><aside className="question-nav"><span className="eyebrow">QUESTIONS</span>{interview.questions.map((item, index) => <button key={item.id} onClick={() => setActiveIndex(index)} className={index === activeIndex ? 'question-number active' : answers[item.id]?.answerId ? 'question-number done' : 'question-number'}>{index + 1}<small>{item.questionType.replace('_', ' ')}</small></button>)}</aside>
      <article className="question-card"><span className="category-chip">{question.questionType.replace('_', ' ')}</span><h2>{question.question}</h2><textarea value={answers[question.id]?.text ?? ''} onChange={(event) => setAnswers({ ...answers, [question.id]: { ...answers[question.id], text: event.target.value } })} placeholder="Structure your answer clearly. Include your reasoning and a concrete example when possible." maxLength="5000" /><div className="answer-actions"><span>{answers[question.id]?.text.length ?? 0}/5000</span><button className="secondary-button" disabled={saving} onClick={() => saveAnswer(question.id).catch((caughtError) => setError(caughtError.message))}>Save answer</button></div>{error && <p className="form-error">{error}</p>}<div className="question-footer"><button className="text-action" disabled={activeIndex === 0} onClick={() => setActiveIndex(activeIndex - 1)}>← Previous</button>{activeIndex < interview.questions.length - 1 ? <button className="primary-button" onClick={() => setActiveIndex(activeIndex + 1)}>Next question →</button> : <button className="primary-button" disabled={saving} onClick={complete}>{saving ? 'Finishing…' : 'Finish & see results →'}</button>}</div></article></div>
  </section>;
}

function Results({ result, onStartInterview }) {
  if (!result) return <section className="page"><PageHeader eyebrow="RESULTS" title="No result selected." subtitle="Complete a mock interview to see a detailed assessment." /><button className="primary-button" onClick={onStartInterview}>Start an interview →</button></section>;
  return <section className="page"><PageHeader eyebrow="INTERVIEW RESULTS" title="Your feedback is in." subtitle="Use the specifics below to guide your next practice session." />
    <div className="result-overview"><div className="score-ring" style={{ '--score': `${result.score}%` }}><div><strong>{Math.round(result.score)}%</strong><span>overall score</span></div></div><div><h2>{result.strongAreas}</h2><p>{result.improvementAreas}</p><button className="primary-button" onClick={onStartInterview}>Try another interview →</button></div></div>
    <div className="feedback-list">{result.answerFeedback.map((feedback, index) => <article className="feedback-card" key={feedback.questionId}><div className="feedback-title"><span>Question {index + 1}</span><strong>{Math.round(feedback.score)}%</strong><small>{feedback.category}</small></div><div><h3>What worked</h3><ul>{feedback.strengths.map((item) => <li key={item}>{item}</li>)}</ul></div><div><h3>Next improvement</h3><ul>{feedback.improvements.map((item) => <li key={item}>{item}</li>)}</ul></div></article>)}</div>
  </section>;
}

function PageHeader({ eyebrow, title, subtitle }) { return <header className="page-header"><span className="eyebrow">{eyebrow}</span><h1>{title}</h1><p>{subtitle}</p></header>; }
function EmptyState({ title, body }) { return <div className="empty-state"><h2>{title}</h2><p>{body}</p></div>; }
